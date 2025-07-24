package kr.hhplus.be.server.application.port

import kr.hhplus.be.server.domain.Reservation

internal interface ReservationPort {
    fun save(reservation: Reservation)
}
