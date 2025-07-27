package kr.hhplus.be.server.adapter.persistence.repository

import org.springframework.stereotype.Repository

@Repository
internal class UserDomainRepository(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {
    override fun exists(userId: String): Boolean = userJpaRepository.existsByUserId(userId)
}
