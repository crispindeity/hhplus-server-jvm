package kr.hhplus.be.server.fake

import kr.hhplus.be.server.pointtransaction.application.port.PointTransactionPort
import kr.hhplus.be.server.pointtransaction.domain.PointTransaction

internal class FakePointTransactionPort : PointTransactionPort {
    private val storage: MutableMap<Long, PointTransaction> = mutableMapOf()
    private var sequence: Long = 0L

    override fun save(pointTransaction: PointTransaction) {
        if (pointTransaction.id == 0L || storage[pointTransaction.id] == null) {
            val newPointTransaction: PointTransaction = pointTransaction.copy(id = sequence++)
            storage[newPointTransaction.id] = newPointTransaction
        } else {
            storage[pointTransaction.id] = pointTransaction
        }
    }
}
