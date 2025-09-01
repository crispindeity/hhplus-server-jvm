package kr.hhplus.be.server.fake

import kr.hhplus.be.server.reservation.application.event.ReservationEvent
import kr.hhplus.be.server.reservation.application.port.ReservationEventPort

internal class FakeReservationEventPort : ReservationEventPort {
    override fun makeReservationEventPublish(event: ReservationEvent) {}
}
