package kr.hhplus.be.server.concertschedule.application.port

import java.time.LocalDate
import kr.hhplus.be.server.concertschedule.domain.ConcertSchedule

internal interface ConcertSchedulePort {
    fun getAvailableSchedules(concertId: Long): List<ConcertSchedule>

    fun getSchedule(
        concertId: Long,
        date: LocalDate
    ): ConcertSchedule?

    fun getSchedule(scheduleId: Long): ConcertSchedule?

    fun decreaseSeatCount(
        concertId: Long,
        scheduleId: Long
    ): Long
}
