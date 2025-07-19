package kr.hhplus.be.server.adapter.web.dto.response

import java.time.LocalDate

internal data class FindAvailableDatesResponse(
    val dates: List<LocalDate>
)
