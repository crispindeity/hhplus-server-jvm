package kr.hhplus.be.server.fake

import java.util.UUID
import kr.hhplus.be.server.application.port.ReservationPort
import kr.hhplus.be.server.domain.Reservation

internal class FakeReservationPort : ReservationPort {
    private val storage: MutableMap<Long, Reservation> = mutableMapOf()
    private var sequence = 0L

    override fun save(reservation: Reservation) {
        if (reservation.id == 0L || storage[reservation.id] == null) {
            val newReservation: Reservation = reservation.copy(id = sequence++)
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
