package kr.hhplus.be.server.seat.application.event

import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.transactional.AfterCommitExecutor
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.reservation.application.event.ReservationEvent
import kr.hhplus.be.server.seat.application.port.SeatEventPort
import kr.hhplus.be.server.seathold.application.port.SeatHoldPort
import kr.hhplus.be.server.seathold.domain.SeatHold
import org.slf4j.Logger
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class SeatEventReader(
    private val seatHoldPort: SeatHoldPort,
    private val afterCommitExecutor: AfterCommitExecutor,
    private val seatEventPort: SeatEventPort,
    private val transactional: Transactional
) {
    private val logger: Logger = Log.getLogger(this.javaClass)

    @EventListener
    fun handleMakeReservationEvent(event: ReservationEvent) {
        runCatching {
            Log.logging(logger) { log ->
                log["method"] = "seat.handleMakeReservationEvent()"
                log["eventId"] = event.eventId
                transactional.run {
                    afterCommitExecutor.registerAfterRollback {
                        sendToSeatHoldFailedEvent(event)
                    }

                    seatHoldPort.save(
                        SeatHold(
                            concertSeatId = event.concertSeatId,
                            userId = event.userId
                        )
                    )
                    afterCommitExecutor.registerAfterCommit {
                        seatEventPort.holdSeatCompletedEventPublish(
                            SeatHoldCompletedEvent(
                                eventId = event.eventId,
                                reservationId = event.reservationId,
                                seatId = event.seatId
                            )
                        )
                    }
                }
            }
        }.onFailure { exception ->
            Log.errorLogging(logger, exception) {}
        }
    }

    private fun sendToSeatHoldFailedEvent(event: ReservationEvent) {
        Log.logging(logger) { log ->
            log["method"] = "sendToSeatHoldFailedEvent()"
            log["eventId"] = event.eventId
            seatEventPort.holdSeatFailEventPublish(
                SeatHoldFailedEvent(
                    eventId = event.eventId,
                    reservationId = event.reservationId,
                    seatId = event.seatId
                )
            )
        }
    }
}
