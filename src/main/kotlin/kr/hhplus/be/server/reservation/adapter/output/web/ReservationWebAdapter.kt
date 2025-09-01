package kr.hhplus.be.server.reservation.adapter.output.web

import kr.hhplus.be.server.reservation.adapter.output.web.dto.ReservationInfoRequest
import kr.hhplus.be.server.reservation.adapter.output.web.executor.HttpExecutor
import kr.hhplus.be.server.reservation.application.port.ReservationWebPort
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component

@Component
internal class ReservationWebAdapter(
    private val httpExecutor: HttpExecutor
) : ReservationWebPort {
    override fun sendReservationInfo(request: ReservationInfoRequest): HttpStatusCode? =
        httpExecutor.sendReservationInfo(request)
}
