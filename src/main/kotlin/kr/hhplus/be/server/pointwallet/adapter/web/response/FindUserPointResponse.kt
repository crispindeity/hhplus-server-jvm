package kr.hhplus.be.server.pointwallet.adapter.web.response

import java.util.UUID

internal data class FindUserPointResponse(
    val userId: UUID,
    val balance: Long
)
