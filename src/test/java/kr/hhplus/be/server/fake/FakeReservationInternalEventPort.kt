package kr.hhplus.be.server.fake

import kr.hhplus.be.server.reservation.application.event.ReservationEvent
import kr.hhplus.be.server.reservation.application.port.ReservationInternalEventPort

internal class FakeReservationInternalEventPort : ReservationInternalEventPort {
    override fun publishReservation(event: ReservationEvent) {}
}
