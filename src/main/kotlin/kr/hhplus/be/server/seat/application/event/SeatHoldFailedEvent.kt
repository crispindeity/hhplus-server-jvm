package kr.hhplus.be.server.seat.application.event

import java.util.UUID

data class SeatHoldFailedEvent(
    val eventId: UUID,
    val reservationId: Long,
    val seatId: Long
)
