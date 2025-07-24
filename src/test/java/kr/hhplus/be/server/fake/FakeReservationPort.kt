package kr.hhplus.be.server.fake

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
}
