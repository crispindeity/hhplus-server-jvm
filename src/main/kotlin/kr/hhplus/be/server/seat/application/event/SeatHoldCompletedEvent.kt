package kr.hhplus.be.server.seat.application.event

import java.util.UUID

internal data class SeatHoldCompletedEvent(
    val eventId: UUID,
    val reservationId: Long,
    val seatId: Long
)
