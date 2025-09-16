package kr.hhplus.be.server.fake

import kr.hhplus.be.server.reservation.application.event.ReservationEvent
import kr.hhplus.be.server.reservation.application.port.ReservationExternalEventPort

internal class FakeReservationExternalEventPort : ReservationExternalEventPort {
    override fun publishReservation(event: ReservationEvent) {}
}
