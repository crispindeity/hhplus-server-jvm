package kr.hhplus.be.server.application.service

import java.time.LocalDate
import java.util.UUID
import kr.hhplus.be.server.adapter.web.dto.response.MakeReservationResponse
import kr.hhplus.be.server.application.port.ConcertSeatPort
import kr.hhplus.be.server.application.port.PaymentPort
import kr.hhplus.be.server.application.port.ReservationPort
import kr.hhplus.be.server.application.port.SeatHoldPort
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.domain.ConcertSeat
import kr.hhplus.be.server.domain.Payment
import kr.hhplus.be.server.domain.Reservation
import kr.hhplus.be.server.domain.SeatHold
import org.slf4j.Logger

internal class ReservationService(
    private val seatHoldPort: SeatHoldPort,
    private val concertSeatPort: ConcertSeatPort,
    private val reservationPort: ReservationPort,
    private val paymentPort: PaymentPort,
    private val reservationContextLoader: ReservationContextLoader
) {
    private val logger: Logger = Log.getLogger(ReservationService::class.java)

    fun makeReservation(
        date: LocalDate,
        concertSeatId: Long,
        userId: String
    ): MakeReservationResponse =
        Log.logging(logger) { log ->
            log["method"] = "makeReservation()"

            val userUUID: UUID = UUID.fromString(userId)
            val context: ReservationContext = reservationContextLoader.load(concertSeatId, date)
            val heldSeat: ConcertSeat = context.concertSeat.held()

            concertSeatPort.update(heldSeat)

            seatHoldPort.save(
                SeatHold(
                    concertSeatId = concertSeatId,
                    userId = userUUID
                )
            )

            val reservation =
                Reservation(
                    userId = userUUID,
                    concertSeatId = concertSeatId,
                    concertId = context.schedule.concertId,
                    status = Reservation.Status.IN_PROGRESS
                )
            reservationPort.save(reservation)

            paymentPort.save(
                Payment(
                    userId = userUUID,
                    price = context.seat.price
                )
            )

            MakeReservationResponse(
                userId = userId,
                concertSeatId = context.schedule.concertId,
                reservedAt = reservation.reservedAt,
                expiresAt = reservation.expiresAt,
                concertDate = context.schedule.date
            )
        }
}
