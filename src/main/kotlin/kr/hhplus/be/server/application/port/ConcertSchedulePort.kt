package kr.hhplus.be.server.application.port

import java.time.LocalDate
import kr.hhplus.be.server.domain.ConcertSchedule

internal interface ConcertSchedulePort {
    fun getAvailableSchedules(concertId: Long): List<ConcertSchedule>

    fun getSchedule(
        concertId: Long,
        date: LocalDate
    ): ConcertSchedule?
}
