package kr.hhplus.be.server.reservation.adapter.persistence.repository

import java.time.LocalDateTime
import kr.hhplus.be.server.reservation.adapter.persistence.entity.ReservationEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface ReservationJpaRepository : JpaRepository<ReservationEntity, Long> {
    fun findAllByUserIdAndStatus(
        userId: String,
        status: ReservationEntity.Status = ReservationEntity.Status.IN_PROGRESS
    ): List<ReservationEntity>

    fun findAllByReservedAtBetweenAndStatus(
        start: LocalDateTime,
        end: LocalDateTime,
        status: ReservationEntity.Status = ReservationEntity.Status.IN_PROGRESS
    ): List<ReservationEntity>
}
