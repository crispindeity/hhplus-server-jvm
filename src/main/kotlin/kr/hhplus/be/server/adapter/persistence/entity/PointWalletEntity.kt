package kr.hhplus.be.server.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
internal class PointWalletEntity(
    override val id: Long? = null,
    @Column(nullable = false, length = 36)
    val userId: String,
    @Column(nullable = false)
    val balance: Long
) : BaseEntity()
