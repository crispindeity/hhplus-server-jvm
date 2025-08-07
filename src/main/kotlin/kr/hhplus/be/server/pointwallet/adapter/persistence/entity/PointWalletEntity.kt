package kr.hhplus.be.server.pointwallet.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.hhplus.be.server.common.adapter.persistence.entity.BaseEntity
import kr.hhplus.be.server.common.adapter.persistence.entity.Version

@Entity
@Table(name = "point_wallets")
internal class PointWalletEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, length = 36)
    val userId: String,
    @Column(nullable = false)
    val balance: Long,
    @jakarta.persistence.Version
    @Column(nullable = false)
    var version: Version = Version(0)
) : BaseEntity()
