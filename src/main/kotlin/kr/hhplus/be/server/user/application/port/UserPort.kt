package kr.hhplus.be.server.user.application.port

import java.util.UUID

internal interface UserPort {
    fun exists(userId: UUID): Boolean
}
