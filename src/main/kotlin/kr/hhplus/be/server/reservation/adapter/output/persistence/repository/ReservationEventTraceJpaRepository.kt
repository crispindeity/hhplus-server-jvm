package kr.hhplus.be.server.reservation.adapter.output.persistence.repository

import kr.hhplus.be.server.reservation.adapter.output.persistence.entity.ReservationEventTraceEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface ReservationEventTraceJpaRepository :
    JpaRepository<ReservationEventTraceEntity, Long> {
    fun countByEventId(eventId: String): Long
}
