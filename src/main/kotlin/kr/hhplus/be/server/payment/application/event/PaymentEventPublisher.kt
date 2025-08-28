package kr.hhplus.be.server.payment.application.event

import kr.hhplus.be.server.common.application.extensions.orThrow
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.transactional.AfterCommitExecutor
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.payment.application.port.PaymentPort
import kr.hhplus.be.server.payment.domain.Payment
import kr.hhplus.be.server.reservation.application.event.MakeReservationEvent
import kr.hhplus.be.server.seat.application.port.SeatPort
import kr.hhplus.be.server.seat.domain.Seat
import kr.hhplus.be.server.seat.exception.SeatException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
internal class PaymentEventPublisher(
    private val paymentPort: PaymentPort,
    private val transactional: Transactional,
    private val afterCommitExecutor: AfterCommitExecutor,
    private val eventPublisher: ApplicationEventPublisher,
    private val seatPort: SeatPort
) {
    private val logger = Log.getLogger(this.javaClass)

    @Async
    @EventListener
    fun handleMakeReservationEvent(event: MakeReservationEvent) {
        runCatching {
            Log.logging(logger) { log ->
                log["method"] = "concertSeat.handleMakeReservationEvent()"
                log["eventId"] = event.eventId
                transactional.run {
                    val seat: Seat =
                        seatPort
                            .getSeat(event.seatId)
                            .orThrow { SeatException(ErrorCode.NOT_FOUND_SEAT) }

                    val paymentId: Long =
                        paymentPort.save(
                            Payment(
                                userId = event.userId,
                                price = seat.price
                            )
                        )

                    afterCommitExecutor.registerAfterCommit {
                        eventPublisher.publishEvent(
                            PaymentSaveCompletedEvent(
                                eventId = event.eventId,
                                reservationId = event.reservationId,
                                paymentId = paymentId
                            )
                        )
                    }
                    afterCommitExecutor.registerAfterRollback {
                        sendToPaymentSaveFailedEvent(event)
                    }
                }
            }
        }.onFailure { exception ->
            Log.errorLogging(logger, exception) {}
        }
    }

    private fun sendToPaymentSaveFailedEvent(event: MakeReservationEvent) {
        eventPublisher.publishEvent(
            PaymentSaveFailedEvent(
                eventId = event.eventId,
                reservationId = event.reservationId
            )
        )
    }
}
