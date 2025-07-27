package kr.hhplus.be.server.pointwallet.adapter.persistence.extensions

import java.util.UUID
import kr.hhplus.be.server.pointwallet.adapter.persistence.entity.PointWalletEntity
import kr.hhplus.be.server.pointwallet.domain.PointWallet

internal fun PointWalletEntity.toDomain(): PointWallet =
    PointWallet(
        id = this.id!!,
        userId = UUID.fromString(this.userId),
        balance = this.balance
    )

internal fun PointWallet.toEntity(): PointWalletEntity =
    PointWalletEntity(
        id = this.id,
        userId = this.userId.toString(),
        balance = this.balance
    )
