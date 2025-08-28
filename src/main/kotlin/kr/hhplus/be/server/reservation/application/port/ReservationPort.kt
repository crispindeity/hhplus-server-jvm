package kr.hhplus.be.server.reservation.application.port

import java.time.LocalDateTime
import kr.hhplus.be.server.reservation.domain.Reservation

internal interface ReservationPort {
    fun save(reservation: Reservation)

    fun getAll(userId: String): List<Reservation>

    fun update(reservation: Reservation)

    fun findAllByRangeAndInProgress(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Reservation>

    fun updateStatusToExpired(ids: List<Long>)

    fun getReservation(reservationId: Long): Reservation?
}
