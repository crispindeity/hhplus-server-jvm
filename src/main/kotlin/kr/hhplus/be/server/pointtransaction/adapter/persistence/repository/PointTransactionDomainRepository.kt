package kr.hhplus.be.server.pointtransaction.adapter.persistence.repository

import kr.hhplus.be.server.pointtransaction.adapter.persistence.entity.PointTransactionEntity
import org.springframework.stereotype.Repository

@Repository
internal class PointTransactionDomainRepository(
    private val jpaRepository: PointTransactionJpaRepository
) : PointTransactionRepository {
    override fun save(entity: PointTransactionEntity) {
        jpaRepository.save(entity)
    }
}
