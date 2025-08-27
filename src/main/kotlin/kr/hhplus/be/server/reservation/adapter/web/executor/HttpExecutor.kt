package kr.hhplus.be.server.reservation.adapter.web.executor

import kr.hhplus.be.server.reservation.application.event.MakeReservationEvent
import org.springframework.http.HttpStatusCode

internal interface HttpExecutor {
    fun sendReservationInfo(request: MakeReservationEvent): HttpStatusCode?
}
