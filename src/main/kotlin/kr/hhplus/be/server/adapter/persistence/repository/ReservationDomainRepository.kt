package kr.hhplus.be.server.adapter.persistence.repository

import jakarta.persistence.EntityManager
import kr.hhplus.be.server.adapter.persistence.entity.ReservationEntity
import org.springframework.stereotype.Repository

@Repository
internal class ReservationDomainRepository(
    private val entityManager: EntityManager,
    private val jpaRepository: ReservationJpaRepository
) : ReservationRepository {
    override fun save(entity: ReservationEntity) {
        jpaRepository.save(entity)
    }

    override fun update(entity: ReservationEntity) {
        entityManager.merge(entity)
    }
}
