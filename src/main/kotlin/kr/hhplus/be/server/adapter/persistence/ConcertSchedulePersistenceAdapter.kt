package kr.hhplus.be.server.adapter.persistence

import java.time.LocalDate
import kr.hhplus.be.server.adapter.persistence.extensions.toDomain
import kr.hhplus.be.server.adapter.persistence.repository.ConcertScheduleRepository
import kr.hhplus.be.server.application.port.ConcertSchedulePort
import kr.hhplus.be.server.domain.ConcertSchedule
import org.springframework.stereotype.Component

@Component
internal class ConcertSchedulePersistenceAdapter(
    private val repository: ConcertScheduleRepository
) : ConcertSchedulePort {
    override fun getAvailableSchedules(concertId: Long): List<ConcertSchedule> =
        repository.findAvailableSchedules(concertId).map { it.toDomain() }

    override fun getSchedule(
        concertId: Long,
        date: LocalDate
    ): ConcertSchedule = repository.findSchedule(concertId, date).toDomain()

    override fun getSchedule(scheduleId: Long): ConcertSchedule? =
        repository.findSchedule(scheduleId)?.toDomain()
}
