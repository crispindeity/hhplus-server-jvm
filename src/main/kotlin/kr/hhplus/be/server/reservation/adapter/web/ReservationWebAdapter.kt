package kr.hhplus.be.server.reservation.adapter.web

import kr.hhplus.be.server.reservation.adapter.web.executor.HttpExecutor
import kr.hhplus.be.server.reservation.application.event.MakeReservationEvent
import kr.hhplus.be.server.reservation.application.port.ReservationWebPort
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component

@Component
internal class ReservationWebAdapter(
    private val httpExecutor: HttpExecutor
) : ReservationWebPort {
    override fun sendReservationInfo(reservationInfo: MakeReservationEvent): HttpStatusCode? =
        httpExecutor.sendReservationInfo(reservationInfo)
}
