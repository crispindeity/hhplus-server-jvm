package kr.hhplus.be.server.pointtransaction.adapter.persistence.extensions

import kr.hhplus.be.server.pointtransaction.adapter.persistence.entity.PointTransactionEntity
import kr.hhplus.be.server.pointtransaction.domain.PointTransaction

internal fun PointTransaction.toEntity(): PointTransactionEntity =
    PointTransactionEntity(
        pointWalletId = this.pointWalletId,
        amount = this.amount,
        type =
            when (this.type) {
                PointTransaction.Type.CHARGED -> PointTransactionEntity.Type.CHARGED
                PointTransaction.Type.USED -> PointTransactionEntity.Type.USED
            }
    )
