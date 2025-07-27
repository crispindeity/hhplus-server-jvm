package kr.hhplus.be.server.concert.adapter.web.response

import java.time.LocalDate

internal data class FindAvailableDatesResponse(
    val dates: List<LocalDate>
)
