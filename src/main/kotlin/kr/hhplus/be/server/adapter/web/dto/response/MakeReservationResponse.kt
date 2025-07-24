package kr.hhplus.be.server.adapter.web.dto.response

import java.time.LocalDate
import java.time.LocalDateTime

internal data class MakeReservationResponse(
    val userId: String,
    val concertSeatId: Long,
    val concertDate: LocalDate,
    val reservedAt: LocalDateTime,
    val expiresAt: LocalDateTime
)
