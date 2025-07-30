package kr.hhplus.be.server.common.time

import java.time.LocalDateTime

data class TimeRange(
    val start: LocalDateTime,
    val end: LocalDateTime
)
