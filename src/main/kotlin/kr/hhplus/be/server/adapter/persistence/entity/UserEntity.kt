package kr.hhplus.be.server.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
internal class UserEntity(
    @Column(nullable = false, length = 36)
    val userId: String
) : BaseEntity()
