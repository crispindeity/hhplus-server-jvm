package kr.hhplus.be.server.pointtransaction.adapter.persistence

import kr.hhplus.be.server.pointtransaction.adapter.persistence.extensions.toEntity
import kr.hhplus.be.server.pointtransaction.adapter.persistence.repository.PointTransactionRepository
import kr.hhplus.be.server.pointtransaction.application.port.PointTransactionPort
import kr.hhplus.be.server.pointtransaction.domain.PointTransaction
import org.springframework.stereotype.Component

@Component
internal class PointTransactionPersistenceAdapter(
    private val repository: PointTransactionRepository
) : PointTransactionPort {
    override fun save(pointTransaction: PointTransaction) {
        repository.save(pointTransaction.toEntity())
    }
}
