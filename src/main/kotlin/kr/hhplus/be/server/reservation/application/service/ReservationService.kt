package kr.hhplus.be.server.reservation.application.service

import java.time.LocalDate
import java.util.UUID
import kr.hhplus.be.server.common.lock.RedisLocks
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.transactional.AfterCommitExecutor
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.reservation.adapter.input.web.response.MakeReservationResponse
import kr.hhplus.be.server.reservation.application.event.ReservationEvent
import kr.hhplus.be.server.reservation.application.port.ReservationExternalEventPort
import kr.hhplus.be.server.reservation.application.port.ReservationInternalEventPort
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.reservation.application.service.extensions.toEvent
import kr.hhplus.be.server.reservation.domain.Reservation
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
internal class ReservationService(
    private val reservationPort: ReservationPort,
    private val reservationContextLoader: ReservationContextLoader,
    private val transactional: Transactional,
    private val afterCommitExecutor: AfterCommitExecutor,
    private val externalEventPort: ReservationExternalEventPort,
    private val internalEventPort: ReservationInternalEventPort
) {
    private val logger: Logger = Log.getLogger(ReservationService::class.java)

    @RedisLocks(
        keys = ["'concertSeat:' + #concertSeatId"],
        waitSeconds = 0,
        leaseSeconds = 3
    )
    fun makeReservation(
        date: LocalDate,
        concertSeatId: Long,
        userId: String
    ): MakeReservationResponse =
        Log.logging(logger) { log ->
            log["method"] = "makeReservation()"

            val userUUID: UUID = UUID.fromString(userId)
            transactional.run {
                val context: ReservationContext =
                    reservationContextLoader.load(concertSeatId, date)

                val reservation =
                    Reservation(
                        userId = userUUID,
                        concertSeatId = concertSeatId,
                        concertId = context.schedule.concertId,
                        status = Reservation.Status.INIT
                    )

                val reservationId: Long = reservationPort.save(reservation)

                afterCommitExecutor.registerAfterCommit {
                    val eventId: UUID = UUID.randomUUID()
                    log["eventId"] = eventId
                    val event: ReservationEvent =
                        context.toEvent(
                            eventId = eventId,
                            userId = UUID.fromString(userId),
                            reservationId = reservationId,
                            reservedAt = reservation.reservedAt
                        )
                    externalEventPort.publishReservation(event)
                    internalEventPort.publishReservation(event)
                }

                MakeReservationResponse(
                    userId = userId,
                    concertSeatId = context.concertSeat.id,
                    reservedAt = reservation.reservedAt,
                    expiresAt = reservation.expiresAt,
                    concertDate = context.schedule.date
                )
            }
        }
}
