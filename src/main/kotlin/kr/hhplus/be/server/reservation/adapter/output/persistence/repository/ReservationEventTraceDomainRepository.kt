package kr.hhplus.be.server.reservation.adapter.output.persistence.repository

import kr.hhplus.be.server.reservation.adapter.output.persistence.entity.ReservationEventTraceEntity
import org.springframework.stereotype.Repository

@Repository
internal class ReservationEventTraceDomainRepository(
    private val jpaRepository: ReservationEventTraceJpaRepository
) : ReservationEventTraceRepository {
    override fun save(entity: ReservationEventTraceEntity) {
        jpaRepository.save(entity)
    }

    override fun count(eventId: String): Long = jpaRepository.countByEventId(eventId)
}
