package kr.hhplus.be.server.common.time

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun LocalDateTime.toFullMinuteRange(): TimeRange {
    val start: LocalDateTime = this.truncatedTo(ChronoUnit.MINUTES)
    val end: LocalDateTime = start.plusSeconds(59).plusNanos(999_999_999)
    return TimeRange(start, end)
}
