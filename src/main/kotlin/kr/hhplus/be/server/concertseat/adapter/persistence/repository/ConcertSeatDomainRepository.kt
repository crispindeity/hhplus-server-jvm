package kr.hhplus.be.server.concertseat.adapter.persistence.repository

import jakarta.persistence.EntityManager
import kr.hhplus.be.server.concertseat.adapter.persistence.dto.AvailableSeatProjection
import kr.hhplus.be.server.concertseat.adapter.persistence.entity.ConcertSeatEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class ConcertSeatDomainRepository(
    private val entityManager: EntityManager,
    private val jpaRepository: ConcertSeatJpaRepository,
    private val jdbcRepository: ConcertSeatJdbcRepository
) : ConcertSeatRepository {
    override fun findAvailableSeats(scheduleId: Long): List<AvailableSeatProjection> =
        jpaRepository.findAvailableSeats(scheduleId)

    override fun findConcertSeat(concertSeatId: Long): ConcertSeatEntity? =
        jpaRepository.findByIdOrNull(concertSeatId)

    override fun update(entity: ConcertSeatEntity) {
        entityManager.merge(entity)
    }

    override fun updateStatusToAvailable(ids: List<Long>) {
        jdbcRepository.updateStatusToAvailable(ids)
    }
}
