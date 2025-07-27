package kr.hhplus.be.server.pointwallet.adapter.web.request

import jakarta.validation.constraints.Min

internal data class ChargePointsRequest(
    @field:Min(value = 0)
    val amount: Long
)
