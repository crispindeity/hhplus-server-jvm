package kr.hhplus.be.server.payment.application.service

import java.util.UUID
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.concertseat.domain.ConcertSeat
import kr.hhplus.be.server.concertseat.exception.ConcertSeatException
import kr.hhplus.be.server.payment.adapter.web.dto.response.PaymentResponse
import kr.hhplus.be.server.payment.application.port.PaymentPort
import kr.hhplus.be.server.payment.domain.Payment
import kr.hhplus.be.server.payment.exception.PaymentException
import kr.hhplus.be.server.pointtransaction.application.port.PointTransactionPort
import kr.hhplus.be.server.pointtransaction.domain.PointTransaction
import kr.hhplus.be.server.pointwallet.application.port.PointWalletPort
import kr.hhplus.be.server.pointwallet.domain.PointWallet
import kr.hhplus.be.server.pointwallet.exception.PointWalletException
import kr.hhplus.be.server.queuetoken.application.port.EntryQueuePort
import kr.hhplus.be.server.queuetoken.domain.QueueToken
import kr.hhplus.be.server.queuetoken.exception.QueueTokenException
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.reservation.domain.Reservation
import kr.hhplus.be.server.reservation.exception.ReservationException
import kr.hhplus.be.server.seathold.application.port.SeatHoldPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
internal class PaymentService(
    private val paymentPort: PaymentPort,
    private val reservationPort: ReservationPort,
    private val pointWalletPort: PointWalletPort,
    private val concertSeatPort: ConcertSeatPort,
    private val entryQueuePort: EntryQueuePort,
    private val seatHoldPort: SeatHoldPort,
    private val pointTransactionPort: PointTransactionPort
) {
    @Transactional
    fun payment(userId: String): PaymentResponse {
        val userUUID: UUID = UUID.fromString(userId)

        val reservations: List<Reservation> =
            reservationPort.getAll(userUUID.toString())
        if (reservations.isEmpty()) {
            throw ReservationException(ErrorCode.NOT_FOUND_RESERVATION)
        }

        val payments: Map<Long, Payment> = loadPayments(reservations)
        val totalPrice: Long = payments.values.sumOf { it.price }

        deductPoint(userUUID, totalPrice)

        reservations.forEach { reservation ->
            val payment: Payment =
                payments[reservation.paymentId]
                    ?: throw PaymentException(ErrorCode.NOT_FOUND_PAYMENT_INFO)
            processReservationAndPayment(reservation, payment)
        }

        completeEntryQueue(userUUID)
        seatHoldPort.deleteAll(reservations.map { it.concertSeatId })

        return PaymentResponse(
            totalPrice = totalPrice,
            reservationCount = reservations.size
        )
    }

    private fun loadPayments(reservations: List<Reservation>): Map<Long, Payment> {
        val paymentIds: List<Long> = reservations.mapNotNull { it.paymentId }.distinct()
        return paymentPort.getAll(paymentIds).associateBy { it.id }
    }

    private fun deductPoint(
        userUUID: UUID,
        totalPrice: Long
    ) {
        val wallet: PointWallet =
            pointWalletPort.getWallet(userUUID)
                ?: throw PointWalletException(ErrorCode.NOT_FOUND_USER_POINT_WALLET)
        val usedWallet: PointWallet = wallet.usePoint(totalPrice)

        pointWalletPort.update(usedWallet)
        pointTransactionPort.save(
            PointTransaction(
                pointWalletId = usedWallet.id,
                amount = totalPrice,
                type = PointTransaction.Type.USED
            )
        )
    }

    private fun processReservationAndPayment(
        reservation: Reservation,
        payment: Payment
    ) {
        if (payment.status != Payment.Status.PENDING) {
            throw PaymentException(ErrorCode.ALREADY_PAYMENT)
        }

        val completedPayment: Payment = payment.complete()
        paymentPort.update(completedPayment)

        val confirmedReservation: Reservation = reservation.confirm()
        reservationPort.update(confirmedReservation)

        val concertSeat: ConcertSeat =
            concertSeatPort.getConcertSeat(reservation.concertSeatId)
                ?: throw ConcertSeatException(ErrorCode.NOT_FOUND_CONCERT_SEAT)
        concertSeat.reserved()
        concertSeatPort.update(concertSeat)
    }

    private fun completeEntryQueue(userUUID: UUID) {
        val token: QueueToken =
            entryQueuePort.getEntryQueueToken(userUUID)
                ?: throw QueueTokenException(ErrorCode.NOT_FOUND_QUEUE_TOKEN)
        val completed: QueueToken = token.completed()
        entryQueuePort.update(completed)
    }
}
