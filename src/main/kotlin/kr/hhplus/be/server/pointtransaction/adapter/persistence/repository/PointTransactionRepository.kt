package kr.hhplus.be.server.pointtransaction.adapter.persistence.repository

import kr.hhplus.be.server.pointtransaction.adapter.persistence.entity.PointTransactionEntity

internal interface PointTransactionRepository {
    fun save(entity: PointTransactionEntity)
}
