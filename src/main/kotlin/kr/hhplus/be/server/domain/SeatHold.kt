package kr.hhplus.be.server.domain

import java.time.LocalDateTime
import java.util.UUID

internal data class SeatHold(
    val id: Long = 0L,
    val concertSeatId: Long,
    val userId: UUID,
    val heldAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime = LocalDateTime.now().plusMinutes(6)
)
