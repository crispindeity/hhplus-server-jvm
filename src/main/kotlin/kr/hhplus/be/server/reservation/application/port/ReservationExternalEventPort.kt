package kr.hhplus.be.server.reservation.application.port

import kr.hhplus.be.server.reservation.application.event.ReservationEvent

internal interface ReservationExternalEventPort {
    fun publishReservation(event: ReservationEvent)
}
