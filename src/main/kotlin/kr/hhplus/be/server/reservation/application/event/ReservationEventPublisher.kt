package kr.hhplus.be.server.reservation.application.event

import kr.hhplus.be.server.common.application.extensions.orThrow
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concertseat.application.event.ConcertSeatHoldFailedEvent
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.reservation.application.port.ReservationWebPort
import kr.hhplus.be.server.reservation.domain.Reservation
import kr.hhplus.be.server.reservation.exception.ReservationException
import org.slf4j.Logger
import org.springframework.context.event.EventListener
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component

@Component
internal class ReservationEventPublisher(
    private val reservationWebPort: ReservationWebPort,
    private val reservationPort: ReservationPort,
    private val transactional: Transactional
) {
    private val logger: Logger = Log.getLogger(this.javaClass)

    @EventListener
    fun handleMakeReservationEvent(event: MakeReservationEvent) {
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

    @EventListener
    fun handleConcertSeatHoldFailedEvent(event: ConcertSeatHoldFailedEvent) {
        runCatching {
            Log.warnLogging(logger) { log ->
                log["method"] = "handleConcertSeatHoldFailedEvent()"
                log["eventId"] = event.eventId
                log["reservationId"] = event.reservationId
                updateReservationStatusAsError(event)
            }
        }.onFailure { exception ->
            Log.errorLogging(logger, exception) {}
        }
    }

    private fun updateReservationStatusAsError(event: ConcertSeatHoldFailedEvent) {
        Log.logging(logger) {
            val foundReservation: Reservation =
                reservationPort
                    .getReservation(event.reservationId)
                    .orThrow { ReservationException(ErrorCode.NOT_FOUND_RESERVATION) }

            if (foundReservation.isStatusAsError()) {
                return@logging
            }

            val updatedReservation: Reservation = foundReservation.error()

            try {
                transactional.run {
                    reservationPort.update(updatedReservation)
                }
            } catch (_: OptimisticLockingFailureException) {
                return@logging
            }
        }
    }
}
