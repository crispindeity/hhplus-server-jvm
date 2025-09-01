package kr.hhplus.be.server.reservation.application.port

import kr.hhplus.be.server.reservation.application.event.ReservationEvent

internal interface ReservationEventPort {
    fun makeReservationEventPublish(event: ReservationEvent)
}
