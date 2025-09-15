package kr.hhplus.be.server.reservation.adapter.output.persistence.repository

import java.time.LocalDateTime
import kr.hhplus.be.server.reservation.adapter.output.persistence.entity.ReservationEntity

internal interface ReservationRepository {
    fun save(entity: ReservationEntity): Long

    fun update(entity: ReservationEntity)

    fun findAll(userId: String): List<ReservationEntity>

    fun updateStatusToExpired(ids: List<Long>)

    fun findAllByRangeAndInProgress(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<ReservationEntity>

    fun findBy(reservationId: Long): ReservationEntity?
}
