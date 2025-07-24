package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.dto.AvailableSeatProjection
import org.springframework.stereotype.Repository

@Repository
internal class ConcertSeatDomainRepository(
    private val jpaRepository: ConcertSeatJpaRepository
) : ConcertSeatRepository {
    override fun findAvailableSeats(scheduleId: Long): List<AvailableSeatProjection> =
        jpaRepository.findAvailableSeats(scheduleId)
}
