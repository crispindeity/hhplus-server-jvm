package kr.hhplus.be.server.fake

import kr.hhplus.be.server.reservation.application.event.MakeReservationEvent
import kr.hhplus.be.server.reservation.application.port.ReservationWebPort
import org.springframework.http.HttpStatusCode

internal class FakeReservationWebPort(
    private val responder: (MakeReservationEvent) -> HttpStatusCode?
) : ReservationWebPort {
    var callCount = 0
    var lastEvent: MakeReservationEvent? = null

    override fun sendReservationInfo(reservationInfo: MakeReservationEvent): HttpStatusCode? {
        callCount += 1
        lastEvent = reservationInfo
        return responder(reservationInfo)
    }
}
