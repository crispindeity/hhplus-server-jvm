package kr.hhplus.be.server.application.service

import java.util.UUID
import kr.hhplus.be.server.adapter.web.dto.response.PaymentResponse
import kr.hhplus.be.server.application.port.ConcertSeatPort
import kr.hhplus.be.server.application.port.EntryQueuePort
import kr.hhplus.be.server.application.port.PaymentPort
import kr.hhplus.be.server.application.port.PointWalletPort
import kr.hhplus.be.server.application.port.ReservationPort
import kr.hhplus.be.server.application.port.SeatHoldPort
import kr.hhplus.be.server.common.exception.ConcertSeatException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.exception.PaymentException
import kr.hhplus.be.server.common.exception.PointWalletException
import kr.hhplus.be.server.common.exception.QueueTokenException
import kr.hhplus.be.server.common.exception.ReservationException
import kr.hhplus.be.server.domain.ConcertSeat
import kr.hhplus.be.server.domain.Payment
import kr.hhplus.be.server.domain.PointWallet
import kr.hhplus.be.server.domain.QueueToken
import kr.hhplus.be.server.domain.Reservation
import org.springframework.stereotype.Service

@Service
internal class PaymentService(
    private val paymentPort: PaymentPort,
    private val reservationPort: ReservationPort,
    private val pointWalletPort: PointWalletPort,
    private val concertSeatPort: ConcertSeatPort,
    private val entryQueuePort: EntryQueuePort,
    private val seatHoldPort: SeatHoldPort
) {
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
            concertSeatPort.getConcertSeat(reservation.concertId)
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
