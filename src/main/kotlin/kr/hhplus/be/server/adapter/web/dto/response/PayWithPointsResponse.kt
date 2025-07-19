package kr.hhplus.be.server.adapter.web.dto.response

import java.time.LocalDateTime

internal data class PayWithPointsResponse(
    val userId: Long,
    val reservationId: Long,
    val price: Long,
    val paidAt: LocalDateTime
)
