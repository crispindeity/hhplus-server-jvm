package kr.hhplus.be.server.application.port

import kr.hhplus.be.server.domain.Seat

internal interface SeatPort {
    fun getAllSeat(seatIds: List<Long>): List<Seat>

    fun getSeat(id: Long): Seat?
}
