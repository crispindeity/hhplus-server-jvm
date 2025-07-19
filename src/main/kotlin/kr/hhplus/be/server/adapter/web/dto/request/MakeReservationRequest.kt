package kr.hhplus.be.server.adapter.web.dto.request

import java.time.LocalDate
import java.time.LocalTime

internal data class MakeReservationRequest(
    val date: LocalDate,
    val time: LocalTime,
    val seat: Long
)
