package kr.hhplus.be.server.adapter.web.dto.response

import java.time.LocalDateTime

internal data class MakeReservationResponse(
    val id: Long,
    val userId: Long,
    val concertId: Long,
    val reservedAt: LocalDateTime,
    val expiresAt: LocalDateTime
)
