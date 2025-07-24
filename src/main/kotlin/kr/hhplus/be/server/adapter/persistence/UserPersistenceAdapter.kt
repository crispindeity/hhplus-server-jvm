package kr.hhplus.be.server.adapter.persistence

import java.util.UUID
import kr.hhplus.be.server.adapter.persistence.repository.UserRepository
import kr.hhplus.be.server.application.port.UserPort
import org.springframework.stereotype.Component

@Component
internal class UserPersistenceAdapter(
    private val repository: UserRepository
) : UserPort {
    override fun exists(userId: UUID): Boolean = repository.exists(userId.toString())
}
