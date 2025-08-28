package kr.hhplus.be.server.reservation.application.port

import kr.hhplus.be.server.reservation.domain.ReservationEventTrace

internal interface ReservationEventTracePort {
    fun save(reservationEventTrace: ReservationEventTrace)
}
