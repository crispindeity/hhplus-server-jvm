package kr.hhplus.be.server.reservation.application.event.extensions

import kr.hhplus.be.server.reservation.adapter.output.web.dto.ReservationInfoRequest
import kr.hhplus.be.server.reservation.application.event.ReservationEvent

internal fun ReservationEvent.toRequest(): ReservationInfoRequest =
    ReservationInfoRequest(
        eventId = this.eventId,
        userId = this.userId,
        reservationId = this.reservationId,
        seatId = this.seatId,
        concertSeatId = this.concertSeatId,
        scheduleId = this.scheduleId,
        reservedAt = this.reservedAt
    )
