package kr.hhplus.be.server.adapter.persistence.repository

import java.time.LocalDate
import kr.hhplus.be.server.adapter.persistence.entity.ConcertScheduleEntity

internal interface ConcertScheduleRepository {
    fun findAvailableSchedules(concertId: Long): List<ConcertScheduleEntity>

    fun findSchedule(
        concertId: Long,
        date: LocalDate
    ): ConcertScheduleEntity
}
