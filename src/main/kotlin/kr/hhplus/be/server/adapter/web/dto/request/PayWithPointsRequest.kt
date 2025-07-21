package kr.hhplus.be.server.adapter.web.dto.request

import jakarta.validation.constraints.Min

internal data class PayWithPointsRequest(
    @field:Min(value = 0)
    val price: Long
)
