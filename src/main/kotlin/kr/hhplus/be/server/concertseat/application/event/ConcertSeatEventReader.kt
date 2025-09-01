package kr.hhplus.be.server.concertseat.application.event

import kr.hhplus.be.server.common.application.extensions.orThrow
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.transactional.AfterCommitExecutor
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatEventPort
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.concertseat.domain.ConcertSeat
import kr.hhplus.be.server.concertseat.exception.ConcertSeatException
import kr.hhplus.be.server.reservation.application.event.ReservationEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class ConcertSeatEventReader(
    private val concertSeatPort: ConcertSeatPort,
    private val transactional: Transactional,
    private val afterCommitExecutor: AfterCommitExecutor,
    private val concertSeatEventPort: ConcertSeatEventPort
) {
    private val logger = Log.getLogger(this.javaClass)

    @EventListener
    fun handleMakeReservationEvent(event: ReservationEvent) {
        runCatching {
            Log.logging(logger) { log ->
                log["method"] = "concertSeat.handleMakeReservationEvent()"
                log["eventId"] = event.eventId
                transactional.run {
                    afterCommitExecutor.registerAfterRollback {
                        sendToConcertSeatHoldFailedEvent(event)
                    }

                    val concertSeat: ConcertSeat =
                        concertSeatPort
                            .getConcertSeat(event.concertSeatId)
                            .orThrow { ConcertSeatException(ErrorCode.NOT_FOUND_CONCERT_SEAT) }

                    val heldSeat: ConcertSeat = concertSeat.held()
                    concertSeatPort.update(heldSeat)

                    afterCommitExecutor.registerAfterCommit {
                        concertSeatEventPort.holdConcertSeatCompletedEventPublish(
                            ConcertSeatHoldCompletedEvent(
                                eventId = event.eventId,
                                reservationId = event.reservationId,
                                concertSeatId = event.concertSeatId
                            )
                        )
                    }
                }
            }
        }.onFailure { exception ->
            Log.errorLogging(logger, exception) {}
        }
    }

    private fun sendToConcertSeatHoldFailedEvent(event: ReservationEvent) {
        Log.logging(logger) { log ->
            log["method"] = "sendToConcertSeatHoldFailedEvent()"
            log["eventId"] = event.eventId
            concertSeatEventPort.holdConcertSeatFailEventPublish(
                ConcertSeatHoldFailedEvent(
                    eventId = event.eventId,
                    reservationId = event.reservationId,
                    concertSeatId = event.concertSeatId
                )
            )
        }
    }
}
