package kr.hhplus.be.server.adapter.web.dto.request

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Min
import java.time.LocalDate
import java.time.LocalTime

internal data class MakeReservationRequest(
    @field:Future
    val date: LocalDate,
    val time: LocalTime,
    @field:Min(value = 1)
    val seat: Long
)
