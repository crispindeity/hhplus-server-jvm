package kr.hhplus.be.server.concertschedule.domain

import java.time.LocalDate

internal data class ConcertSchedule(
    val id: Long = 0L,
    val concertId: Long,
    val date: LocalDate
)
