package kr.hhplus.be.server.seat.application.port

import kr.hhplus.be.server.seat.domain.Seat

internal interface SeatPort {
    fun getAllSeat(seatIds: List<Long>): List<Seat>

    fun getSeat(id: Long): Seat?
}
