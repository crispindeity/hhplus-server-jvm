package kr.hhplus.be.server.adapter.web.dto.request

import jakarta.validation.constraints.Min

internal data class ChargePointsRequest(
    @field:Min(value = 0)
    val amount: Long
)
