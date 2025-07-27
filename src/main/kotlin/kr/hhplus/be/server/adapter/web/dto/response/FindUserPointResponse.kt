package kr.hhplus.be.server.adapter.web.dto.response

import java.util.UUID

internal data class FindUserPointResponse(
    val userId: UUID,
    val balance: Long
)
