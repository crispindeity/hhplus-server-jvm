package kr.hhplus.be.server.ranking.adapter.persistence.repository

import java.time.LocalDate
import java.time.LocalDateTime

internal interface RankingRepository {
    fun saveFirstReservationTime(
        concertId: Long,
        concertDate: LocalDate,
        reservationAt: LocalDateTime
    )

    fun saveSoldOutTime(
        concertId: Long,
        scheduleId: Long,
        concertDate: LocalDate,
        soldOutAt: LocalDateTime
    )

    fun saveSoldOutRanking(
        concertId: Long,
        scheduleId: Long,
        concertDate: LocalDate
    )
}
