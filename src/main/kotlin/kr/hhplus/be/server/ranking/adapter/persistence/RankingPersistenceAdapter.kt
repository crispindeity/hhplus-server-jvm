package kr.hhplus.be.server.ranking.adapter.persistence

import java.time.LocalDate
import java.time.LocalDateTime
import kr.hhplus.be.server.ranking.adapter.persistence.repository.RankingRepository
import kr.hhplus.be.server.ranking.application.port.RankingPort
import org.springframework.stereotype.Component

@Component
internal class RankingPersistenceAdapter(
    private val repository: RankingRepository
) : RankingPort {
    override fun saveFirstReservationTime(
        concertId: Long,
        concertDate: LocalDate,
        reservationAt: LocalDateTime
    ) {
        repository.saveFirstReservationTime(concertId, concertDate, reservationAt)
    }

    override fun saveSoldOutTime(
        concertId: Long,
        scheduleId: Long,
        concertDate: LocalDate,
        soldOutAt: LocalDateTime
    ) {
        repository.saveSoldOutTime(concertId, scheduleId, concertDate, soldOutAt)
    }

    override fun saveSoldOutRanking(
        concertId: Long,
        scheduleId: Long,
        concertDate: LocalDate
    ) {
        repository.saveSoldOutRanking(concertId, scheduleId, concertDate)
    }
}
