package kr.hhplus.be.server.reservation.application.port

import kr.hhplus.be.server.reservation.domain.Reservation

internal interface ReservationPort {
    fun save(reservation: Reservation)

    fun getAll(userId: String): List<Reservation>

    fun update(reservation: Reservation)
}
