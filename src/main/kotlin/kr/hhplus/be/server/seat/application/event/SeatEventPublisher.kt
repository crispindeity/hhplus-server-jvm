package kr.hhplus.be.server.seat.application.event

import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.transactional.AfterCommitExecutor
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.reservation.application.event.MakeReservationEvent
import kr.hhplus.be.server.seathold.application.port.SeatHoldPort
import kr.hhplus.be.server.seathold.domain.SeatHold
import org.slf4j.Logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
internal class SeatEventPublisher(
    private val seatHoldPort: SeatHoldPort,
    private val afterCommitExecutor: AfterCommitExecutor,
    private val eventPublisher: ApplicationEventPublisher,
    private val transactional: Transactional
) {
    private val logger: Logger = Log.getLogger(this.javaClass)

    @Async
    @EventListener
    fun handleMakeReservationEvent(event: MakeReservationEvent) {
        runCatching {
            Log.logging(logger) { log ->
                log["method"] = "handleMakeReservationEvent()"
                log["eventId"] = event.eventId
                transactional.run {
                    seatHoldPort.save(
                        SeatHold(
                            concertSeatId = event.concertSeatId,
                            userId = event.userId
                        )
                    )
                    afterCommitExecutor.registerAfterCommit {
                        eventPublisher.publishEvent(
                            SeatHoldCompletedEvent(
                                eventId = event.eventId,
                                reservationId = event.reservationId,
                                seatId = event.seatId
                            )
                        )
                    }

                    afterCommitExecutor.registerAfterRollback {
                        sendToSeatHoldFailedEvent(event)
                    }
                }
            }
        }.onFailure { exception ->
            Log.errorLogging(logger, exception) {}
        }
    }

    private fun sendToSeatHoldFailedEvent(event: MakeReservationEvent) {
        eventPublisher.publishEvent(
            SeatHoldFailedEvent(
                eventId = event.eventId,
                reservationId = event.reservationId,
                seatId = event.seatId
            )
        )
    }
}
