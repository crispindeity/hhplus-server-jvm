package kr.hhplus.be.server.adapter.persistence.repository

import java.time.LocalDate
import kr.hhplus.be.server.adapter.persistence.entity.ConcertScheduleEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class ConcertScheduleDomainRepository(
    private val jpaRepository: ConcertScheduleJpaRepository
) : ConcertScheduleRepository {
    override fun findAvailableSchedules(concertId: Long): List<ConcertScheduleEntity> =
        jpaRepository.findAvailableSchedules(concertId)

    override fun findSchedule(
        concertId: Long,
        date: LocalDate
    ): ConcertScheduleEntity = jpaRepository.findByConcertIdAndDate(concertId, date)

    override fun findSchedule(scheduleId: Long): ConcertScheduleEntity? =
        jpaRepository.findByIdOrNull(scheduleId)
}
