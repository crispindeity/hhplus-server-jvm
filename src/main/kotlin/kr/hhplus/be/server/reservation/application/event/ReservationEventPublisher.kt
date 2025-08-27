package kr.hhplus.be.server.reservation.application.event

import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.reservation.application.port.ReservationWebPort
import kr.hhplus.be.server.reservation.application.service.dto.MakeReservationEvent
import kr.hhplus.be.server.reservation.exception.ReservationException
import org.slf4j.Logger
import org.springframework.context.event.EventListener
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component

@Component
internal class ReservationEventPublisher(
    private val reservationWebPort: ReservationWebPort
) {
    private val logger: Logger = Log.getLogger(this.javaClass)

    @EventListener
    fun onMakeReservation(event: MakeReservationEvent) {
        runCatching {
            Log.logging(logger) { log ->
                log["method"] = "onEvent()"
                val status: HttpStatusCode? =
                    reservationWebPort.sendReservationInfo(event)
                if (status == null || !status.is2xxSuccessful) {
                    throw ReservationException(
                        code = ErrorCode.FAILED_SEND_RESERVATION_INFO,
                        message = status?.value().toString()
                    )
                }
            }
        }.onFailure {
            Log.warnLogging(logger) { log ->
                log["eventId"] = event.eventId
                log["reservationId"] = event.reservationId
            }
        }
    }
}
