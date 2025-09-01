package kr.hhplus.be.server.reservation.adapter.output.web.dto

import java.time.LocalDateTime
import java.util.UUID

internal data class ReservationInfoRequest(
    val eventId: UUID,
    val userId: UUID,
    val reservationId: Long,
    val seatId: Long,
    val concertSeatId: Long,
    val scheduleId: Long,
    val reservedAt: LocalDateTime
)
