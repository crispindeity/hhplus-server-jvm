package kr.hhplus.be.server.pointtransaction.application.port

import kr.hhplus.be.server.pointtransaction.domain.PointTransaction

internal interface PointTransactionPort {
    fun save(pointTransaction: PointTransaction)
}
