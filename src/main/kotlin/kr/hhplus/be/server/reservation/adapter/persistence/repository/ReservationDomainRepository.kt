package kr.hhplus.be.server.reservation.adapter.persistence.repository

import jakarta.persistence.EntityManager
import java.time.LocalDateTime
import kr.hhplus.be.server.reservation.adapter.persistence.entity.ReservationEntity
import org.springframework.stereotype.Repository

@Repository
internal class ReservationDomainRepository(
    private val entityManager: EntityManager,
    private val jpaRepository: ReservationJpaRepository,
    private val jdbcRepository: ReservationJdbcRepository
) : ReservationRepository {
    override fun save(entity: ReservationEntity) {
        jpaRepository.save(entity)
    }

    override fun update(entity: ReservationEntity) {
        entityManager.merge(entity)
    }

    override fun findAll(userId: String): List<ReservationEntity> =
        jpaRepository.findAllByUserId(userId)

    override fun updateStatusToExpired(ids: List<Long>) {
        jdbcRepository.updateStatusToExpired(ids)
    }

    override fun findAllByRangeAndInProgress(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<ReservationEntity> = jpaRepository.findAllByReservedAtBetweenAndStatus(start, end)
}
