package kr.hhplus.be.server.user.adapter.persistence.repository

import kr.hhplus.be.server.user.adapter.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun existsByUserId(userId: String): Boolean
}
