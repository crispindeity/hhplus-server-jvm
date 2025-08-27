package kr.hhplus.be.server.reservation.application.port

import kr.hhplus.be.server.reservation.application.service.dto.MakeReservationEvent
import org.springframework.http.HttpStatusCode

internal interface ReservationWebPort {
    fun sendReservationInfo(reservationInfo: MakeReservationEvent): HttpStatusCode?
}
