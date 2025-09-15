package kr.hhplus.be.server.reservation.adapter.input.web.request

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Min
import java.time.LocalDate

internal data class MakeReservationRequest(
    @field:Future
    val date: LocalDate,
    @field:Min(value = 1)
    val seat: Long
)
