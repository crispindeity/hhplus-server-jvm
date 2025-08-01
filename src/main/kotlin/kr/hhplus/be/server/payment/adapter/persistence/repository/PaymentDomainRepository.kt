package kr.hhplus.be.server.payment.adapter.persistence.repository

import jakarta.persistence.EntityManager
import kr.hhplus.be.server.payment.adapter.persistence.entity.PaymentEntity
import org.springframework.stereotype.Repository

@Repository
internal class PaymentDomainRepository(
    private val entityManager: EntityManager,
    private val jpaRepository: PaymentJpaRepository,
    private val jdbcRepository: PaymentJdbcRepository
) : PaymentRepository {
    override fun save(entity: PaymentEntity): Long = jpaRepository.save(entity).id!!

    override fun findAll(ids: List<Long>): List<PaymentEntity> =
        jpaRepository.findAllByIdInAndStatus(ids)

    override fun update(entity: PaymentEntity) {
        entityManager.merge(entity)
    }

    override fun updateStatusToCancelled(ids: List<Long>) {
        jdbcRepository.updateStatusToCancelled(ids)
    }
}
