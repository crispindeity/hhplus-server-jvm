package kr.hhplus.be.server.scheduler.orchestrator

import java.time.LocalDateTime
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.time.TimeRange
import kr.hhplus.be.server.common.time.toFullMinuteRange
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.payment.application.port.PaymentPort
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.reservation.domain.Reservation
import kr.hhplus.be.server.seathold.application.port.SeatHoldPort
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
internal class ReservationOrchestrator(
    private val reservationPort: ReservationPort,
    private val paymentPort: PaymentPort,
    private val seatHoldPort: SeatHoldPort,
    private val concertSeatPort: ConcertSeatPort,
    private val transactional: Transactional
) {
    private val logger: Logger = Log.getLogger(ReservationOrchestrator::class.java)

    companion object {
        private const val EXPIRE_MINUTES = 5L
    }

    fun expireReservations(currentDateTime: LocalDateTime) =
        Log.logging(logger) { log ->
            log["method"] = "expireReservations()"
            val timeRange: TimeRange =
                currentDateTime
                    .minusMinutes(
                        EXPIRE_MINUTES
                    ).toFullMinuteRange()
            val foundReservations: List<Reservation> =
                reservationPort.findAllByRangeAndInProgress(timeRange.start, timeRange.end)
            log["expiredCount"] = foundReservations.size

            if (foundReservations.isEmpty()) {
                return@logging
            }
            cleanupExpiredReservations(foundReservations)
        }

    private fun cleanupExpiredReservations(reservations: List<Reservation>) {
        val ids: List<Long> = reservations.map { it.id }
        val paymentIds: List<Long> = reservations.mapNotNull { it.paymentId }
        val seatIds: List<Long> = reservations.map { it.concertSeatId }

        transactional.run {
            reservationPort.updateStatusToExpired(ids)
            paymentPort.updateStatusToCancelled(paymentIds)
            seatHoldPort.deleteAllByConcertSeatIds(seatIds)
            concertSeatPort.updateStatusToAvailable(seatIds)
        }
    }
}
