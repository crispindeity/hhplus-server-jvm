package kr.hhplus.be.server.pointwallet.adapter.persistence.repository

import kr.hhplus.be.server.pointwallet.adapter.persistence.entity.PointWalletEntity

internal interface PointWalletRepository {
    fun findWallet(userId: String): PointWalletEntity?

    fun update(entity: PointWalletEntity)
}
