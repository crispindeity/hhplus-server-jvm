package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.entity.PointWalletEntity

internal interface PointWalletRepository {
    fun findWallet(userId: String): PointWalletEntity?

    fun update(entity: PointWalletEntity)
}
