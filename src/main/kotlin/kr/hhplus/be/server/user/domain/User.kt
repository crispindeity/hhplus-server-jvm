package kr.hhplus.be.server.user.domain

import java.util.UUID

internal data class User(
    val id: Long = 0L,
    val userId: UUID
)
