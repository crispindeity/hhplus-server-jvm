package kr.hhplus.be.server.fake

import java.time.LocalDateTime
import java.util.UUID
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.reservation.domain.Reservation

internal class FakeReservationPort : ReservationPort {
    private val storage: MutableMap<Long, Reservation> = mutableMapOf()
    private var sequence = 0L

    override fun save(reservation: Reservation) {
        if (reservation.id == 0L || storage[reservation.id] == null) {
            val newReservation: Reservation = reservation.copy(id = ++sequence)
            storage[newReservation.id] = newReservation
        } else {
            storage[reservation.id] = reservation
        }
    }

    override fun getAll(userId: String): List<Reservation> =
        storage.values.filter { it.userId.toString() == userId }.toList()

    override fun update(reservation: Reservation) {
        storage[reservation.id] = reservation
    }

    override fun findAllByRangeAndInProgress(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Reservation> =
        storage.values
            .filter { it.status == Reservation.Status.IN_PROGRESS }
            .filter { it.reservedAt in start..end }
            .toList()

    override fun updateStatusToExpired(ids: List<Long>) {
        ids.forEach { id ->
            val reservation: Reservation? = storage[id]
            if (reservation != null && reservation.status == Reservation.Status.IN_PROGRESS) {
                storage[id] = reservation.copy(status = Reservation.Status.EXPIRED)
            }
        }
    }

    override fun getReservation(reservationId: Long): Reservation? = storage[reservationId]

    fun saveSingleReservation(userId: UUID) {
        storage[1L] =
            Reservation(
                id = 1L,
                userId = userId,
                concertSeatId = 1L,
                concertId = 1L,
                paymentId = 1L,
                status = Reservation.Status.IN_PROGRESS
            )
    }
}
