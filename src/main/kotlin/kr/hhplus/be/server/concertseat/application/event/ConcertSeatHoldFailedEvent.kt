package kr.hhplus.be.server.concertseat.application.event

import java.util.UUID

internal data class ConcertSeatHoldFailedEvent(
    val eventId: UUID,
    val reservationId: Long,
    val concertSeatId: Long
)
