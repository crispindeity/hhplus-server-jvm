package kr.hhplus.be.server.fake

import java.util.UUID
import kr.hhplus.be.server.user.application.port.UserPort
import kr.hhplus.be.server.user.domain.User

class FakeUserPort : UserPort {
    private val storage: MutableMap<Long, User> = mutableMapOf()

    override fun exists(userId: UUID): Boolean = storage.values.any { it.userId == userId }
}
