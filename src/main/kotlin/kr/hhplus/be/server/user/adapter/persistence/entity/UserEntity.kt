package kr.hhplus.be.server.user.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import kr.hhplus.be.server.common.adapter.persistence.entity.BaseEntity

@Entity
internal class UserEntity(
    @Column(nullable = false, length = 36)
    val userId: String
) : BaseEntity()
