package kr.hhplus.be.server.reservation.application.event

import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.reservation.application.event.extensions.toRequest
import kr.hhplus.be.server.reservation.application.port.ReservationWebPort
import kr.hhplus.be.server.reservation.exception.ReservationException
import org.slf4j.Logger
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component

@Component
internal class ReservationEventHandler(
    private val reservationWebPort: ReservationWebPort
) {
    private val logger: Logger = Log.getLogger(this.javaClass)

    fun handleReservationCreatedEvent(event: ReservationEvent) {
        Log.logging(logger) { log ->
            log["method"] = "reservation.handleReservationCreatedEvent()"
            log["eventId"] = event.eventId
            log["reservationId"] = event.reservationId
            val status: HttpStatusCode? =
                reservationWebPort.sendReservationInfo(event.toRequest())
            if (status == null || !status.is2xxSuccessful) {
                throw ReservationException(
                    code = ErrorCode.FAILED_SEND_RESERVATION_INFO,
                    message = status?.value().toString()
                )
            }
        }
    }
}
