package kr.hhplus.be.server.fake

import java.util.UUID
import kr.hhplus.be.server.application.port.UserPort
import kr.hhplus.be.server.domain.User

class FakeUserPort : UserPort {
    private val storage: MutableMap<Long, User> = mutableMapOf()

    override fun exists(userId: UUID): Boolean = storage.values.any { it.userId == userId }
}
