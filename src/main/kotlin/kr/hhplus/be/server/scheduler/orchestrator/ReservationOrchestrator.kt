package kr.hhplus.be.server.scheduler.orchestrator

import java.time.LocalDateTime
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.time.TimeRange
import kr.hhplus.be.server.common.time.toFullMinuteRange
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.payment.application.port.PaymentPort
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.reservation.domain.Reservation
import kr.hhplus.be.server.seathold.application.port.SeatHoldPort
import org.slf4j.Logger
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
internal class ReservationOrchestrator(
    private val reservationPort: ReservationPort,
    private val paymentPort: PaymentPort,
    private val seatHoldPort: SeatHoldPort,
    private val concertSeatPort: ConcertSeatPort
) {
    private val logger: Logger = Log.getLogger(ReservationOrchestrator::class.java)

    companion object {
        private const val EXPIRE_MINUTES = 5L
    }

    @Transactional
    fun expireReservations(currentDateTime: LocalDateTime) =
        Log.logging(logger) { log ->
            log["method"] = "expireReservations()"
            val timeRange: TimeRange =
                currentDateTime
                    .minusMinutes(
                        EXPIRE_MINUTES
                    ).toFullMinuteRange()
            val foundReservations: List<Reservation> =
                reservationPort.findAllRange(timeRange.start, timeRange.end)
            log["expiredCount"] = foundReservations.size

            if (foundReservations.isEmpty()) {
                return@logging
            }
            cleanupExpiredReservations(foundReservations)
        }

    private fun cleanupExpiredReservations(reservations: List<Reservation>) {
        val ids: List<Long> = reservations.map { it.id }
        reservationPort.updateStatusToExpired(ids)

        val paymentIds: List<Long> = reservations.mapNotNull { it.paymentId }
        paymentPort.updateStatusToCancelled(paymentIds)

        val seatIds: List<Long> = reservations.map { it.concertSeatId }
        seatHoldPort.deleteAllByConcertSeatIds(seatIds)
        concertSeatPort.updateStatusToAvailable(seatIds)
    }
}
