package kr.hhplus.be.server.reservation.application.event

import kr.hhplus.be.server.common.application.extensions.orThrow
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concertseat.application.event.ConcertSeatHoldCompletedEvent
import kr.hhplus.be.server.concertseat.application.event.ConcertSeatHoldFailedEvent
import kr.hhplus.be.server.payment.application.event.PaymentSaveCompletedEvent
import kr.hhplus.be.server.payment.application.event.PaymentSaveFailedEvent
import kr.hhplus.be.server.reservation.application.event.extensions.toRequest
import kr.hhplus.be.server.reservation.application.port.ReservationEventTracePort
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.reservation.application.port.ReservationWebPort
import kr.hhplus.be.server.reservation.domain.Reservation
import kr.hhplus.be.server.reservation.domain.ReservationEventTrace
import kr.hhplus.be.server.reservation.exception.ReservationException
import kr.hhplus.be.server.seat.application.event.SeatHoldCompletedEvent
import kr.hhplus.be.server.seat.application.event.SeatHoldFailedEvent
import org.slf4j.Logger
import org.springframework.context.event.EventListener
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatusCode
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
internal class ReservationEventReader(
    private val reservationWebPort: ReservationWebPort,
    private val reservationPort: ReservationPort,
    private val transactional: Transactional,
    private val reservationEventTracePort: ReservationEventTracePort
) {
    private val logger: Logger = Log.getLogger(this.javaClass)

    companion object {
        const val TRACE_COUNT = 3L
    }

    @Async
    @EventListener
    fun handleMakeReservationEvent(event: ReservationEvent) {
        runCatching {
            Log.logging(logger) { log ->
                log["method"] = "reservation.handleMakeReservationEvent()"
                val status: HttpStatusCode? =
                    reservationWebPort.sendReservationInfo(event.toRequest())
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
                updateReservationStatusAsError(event.reservationId)
            }
        }.onFailure { exception ->
            Log.errorLogging(logger, exception) {}
        }
    }

    private fun updateReservationStatusAsError(reservationId: Long) {
        Log.logging(logger) {
            try {
                transactional.run {
                    val foundReservation: Reservation =
                        reservationPort
                            .getReservation(reservationId)
                            .orThrow { ReservationException(ErrorCode.NOT_FOUND_RESERVATION) }

                    if (foundReservation.isStatusAsError()) {
                        return@run
                    }

                    val updatedReservation: Reservation = foundReservation.error()
                    reservationPort.update(updatedReservation)
                }
            } catch (_: OptimisticLockingFailureException) {
                return@logging
            }
        }
    }

    @EventListener
    fun handleConcertSeatHoldCompletedEvent(event: ConcertSeatHoldCompletedEvent) {
        runCatching {
            transactional.run {
                reservationEventTracePort.save(
                    ReservationEventTrace(
                        eventId = event.eventId,
                        reservationId = event.reservationId,
                        eventType = ReservationEventTrace.EventType.CONCERT_SEAT_HELD
                    )
                )

                if (reservationEventTracePort.count(event.eventId) == TRACE_COUNT) {
                    updateReservationStatusAsInProgress(event.reservationId)
                }
            }
        }.onFailure { exception ->
            Log.errorLogging(logger, exception) { log ->
                log["eventId"] = event.eventId
                log["reservationId"] = event.reservationId
                updateReservationStatusAsError(event.reservationId)
            }
        }
    }

    @EventListener
    fun handleSeatHoldFailedEvent(event: SeatHoldFailedEvent) {
        runCatching {
            Log.warnLogging(logger) { log ->
                log["method"] = "handleSeatHoldFailedEvent()"
                log["eventId"] = event.eventId
                log["reservationId"] = event.reservationId
                updateReservationStatusAsError(event.reservationId)
            }
        }.onFailure { exception ->
            Log.errorLogging(logger, exception) {}
        }
    }

    @EventListener
    fun handleSeatHoldCompletedEvent(event: SeatHoldCompletedEvent) {
        runCatching {
            transactional.run {
                reservationEventTracePort.save(
                    ReservationEventTrace(
                        eventId = event.eventId,
                        reservationId = event.reservationId,
                        eventType = ReservationEventTrace.EventType.SEAT_HELD
                    )
                )

                if (reservationEventTracePort.count(event.eventId) == TRACE_COUNT) {
                    updateReservationStatusAsInProgress(event.reservationId)
                }
            }
        }.onFailure { exception ->
            Log.errorLogging(logger, exception) { log ->
                log["eventId"] = event.eventId
                log["reservationId"] = event.reservationId
                updateReservationStatusAsError(event.reservationId)
            }
        }
    }

    @EventListener
    fun handlePaymentSaveFailedEvent(event: PaymentSaveFailedEvent) {
        runCatching {
            Log.warnLogging(logger) { log ->
                log["method"] = "handlePaymentSaveFailedEvent()"
                log["eventId"] = event.eventId
                log["reservationId"] = event.reservationId
                updateReservationStatusAsError(event.reservationId)
            }
        }.onFailure { exception ->
            Log.errorLogging(logger, exception) {}
        }
    }

    @EventListener
    fun handlePaymentSaveCompletedEvent(event: PaymentSaveCompletedEvent) {
        runCatching {
            transactional.run {
                val reservation: Reservation =
                    reservationPort
                        .getReservation(event.reservationId)
                        .orThrow { ReservationException(ErrorCode.NOT_FOUND_RESERVATION) }

                val updatedReservation: Reservation = reservation.updatePayment(event.paymentId)
                reservationPort.update(updatedReservation)

                reservationEventTracePort.save(
                    ReservationEventTrace(
                        eventId = event.eventId,
                        reservationId = event.reservationId,
                        eventType = ReservationEventTrace.EventType.PAYMENT
                    )
                )

                if (reservationEventTracePort.count(event.eventId) == TRACE_COUNT) {
                    updateReservationStatusAsInProgress(event.reservationId)
                }
            }
        }.onFailure { exception ->
            Log.errorLogging(logger, exception) { log ->
                log["eventId"] = event.eventId
                log["reservationId"] = event.reservationId
                updateReservationStatusAsError(event.reservationId)
            }
        }
    }

    private fun updateReservationStatusAsInProgress(reservationId: Long) {
        Log.logging(logger) { log ->
            log["method"] = "updateReservationStatusAsInProgress()"
            try {
                transactional.run {
                    val foundReservation: Reservation =
                        reservationPort
                            .getReservation(reservationId)
                            .orThrow { ReservationException(ErrorCode.NOT_FOUND_RESERVATION) }

                    if (foundReservation.isStatusAsInProgress()) {
                        return@run
                    }

                    val updatedReservation: Reservation = foundReservation.inProgress()
                    reservationPort.update(updatedReservation)
                }
            } catch (_: OptimisticLockingFailureException) {
                return@logging
            }
        }
    }
}
