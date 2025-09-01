package kr.hhplus.be.server.fake

import kr.hhplus.be.server.reservation.adapter.output.web.dto.ReservationInfoRequest
import kr.hhplus.be.server.reservation.application.port.ReservationWebPort
import org.springframework.http.HttpStatusCode

internal class FakeReservationWebPort(
    private val responder: (ReservationInfoRequest) -> HttpStatusCode?
) : ReservationWebPort {
    var callCount = 0
    var infoRequest: ReservationInfoRequest? = null

    override fun sendReservationInfo(request: ReservationInfoRequest): HttpStatusCode? {
        callCount += 1
        infoRequest = request
        return responder(request)
    }
}
