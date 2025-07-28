package kr.hhplus.be.server.pointwallet.adapter.persistence.repository

import kr.hhplus.be.server.pointwallet.adapter.persistence.entity.PointWalletEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface PointWalletJpaRepository : JpaRepository<PointWalletEntity, Long> {
    fun findByUserId(userId: String): PointWalletEntity?
}
