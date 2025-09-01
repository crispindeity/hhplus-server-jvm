package kr.hhplus.be.server.reservation.adapter.output.web.executor

import kr.hhplus.be.server.reservation.adapter.output.web.dto.ReservationInfoRequest
import org.springframework.http.HttpStatusCode

internal interface HttpExecutor {
    fun sendReservationInfo(request: ReservationInfoRequest): HttpStatusCode?
}
