package kr.hhplus.be.server.ranking.application.port

import java.time.LocalDate
import java.time.LocalDateTime

internal interface RankingPort {
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
