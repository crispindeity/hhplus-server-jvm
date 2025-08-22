package kr.hhplus.be.server.fake

import java.time.LocalDate
import kr.hhplus.be.server.concertschedule.application.port.ConcertSchedulePort
import kr.hhplus.be.server.concertschedule.domain.ConcertSchedule
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.concertseat.domain.ConcertSeat

internal class FakeConcertSchedulePort(
    private val concertSeatPort: ConcertSeatPort
) : ConcertSchedulePort {
    private val storage: MutableMap<Long, ConcertSchedule> = mutableMapOf()

    override fun getAvailableSchedules(concertId: Long): List<ConcertSchedule> =
        storage.values
            .filter { it.concertId == concertId }
            .filter { schedule ->
                concertSeatPort
                    .getAvailableSeats(schedule.id)
                    .any { it.status == ConcertSeat.SeatStatus.AVAILABLE }
            }.sortedBy { it.date }

    override fun getSchedule(
        concertId: Long,
        date: LocalDate
    ): ConcertSchedule? = storage.values.find { it.concertId == concertId && it.date == date }

    override fun getSchedule(scheduleId: Long): ConcertSchedule? = storage[scheduleId]

    override fun decreaseSeatCount(
        concertId: Long,
        scheduleId: Long
    ): Long {
        TODO("Not yet implemented")
    }

    fun saveSingleSchedule(id: Long) {
        storage[id] =
            ConcertSchedule(
                id = id,
                concertId = id,
                date = LocalDate.now()
            )
    }
}
