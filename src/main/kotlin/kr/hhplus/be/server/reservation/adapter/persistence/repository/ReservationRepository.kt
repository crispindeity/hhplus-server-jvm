package kr.hhplus.be.server.reservation.adapter.persistence.repository

import java.time.LocalDateTime
import kr.hhplus.be.server.reservation.adapter.persistence.entity.ReservationEntity

internal interface ReservationRepository {
    fun save(entity: ReservationEntity)

    fun update(entity: ReservationEntity)

    fun findAll(userId: String): List<ReservationEntity>

    fun updateStatusToExpired(ids: List<Long>)

    fun findAllByRangeAndInProgress(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<ReservationEntity>
}
