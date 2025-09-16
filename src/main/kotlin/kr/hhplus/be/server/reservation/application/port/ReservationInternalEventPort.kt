package kr.hhplus.be.server.reservation.application.port

import kr.hhplus.be.server.reservation.application.event.ReservationEvent

internal interface ReservationInternalEventPort {
    fun publishReservation(event: ReservationEvent)
}
