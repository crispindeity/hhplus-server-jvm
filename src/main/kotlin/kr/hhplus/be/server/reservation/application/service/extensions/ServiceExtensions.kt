package kr.hhplus.be.server.reservation.application.service.extensions

import java.time.LocalDateTime
import java.util.UUID
import kr.hhplus.be.server.reservation.application.event.MakeReservationEvent
import kr.hhplus.be.server.reservation.application.service.ReservationContext

internal fun ReservationContext.toMakeEvent(
    eventId: UUID,
    userId: UUID,
    reservationId: Long,
    reservedAt: LocalDateTime
): MakeReservationEvent =
    MakeReservationEvent(
        eventId = eventId,
        userId = userId,
        reservationId = reservationId,
        seatId = this.seat.id,
        concertSeatId = this.concertSeat.id,
        scheduleId = this.schedule.id,
        reservedAt = reservedAt
    )
