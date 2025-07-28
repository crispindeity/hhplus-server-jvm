package kr.hhplus.be.server.fake

import kr.hhplus.be.server.seat.application.port.SeatPort
import kr.hhplus.be.server.seat.domain.Seat

internal class FakeSeatPort : SeatPort {
    private val seatStorage = mutableMapOf<Long, Seat>()

    override fun getAllSeat(seatIds: List<Long>): List<Seat> =
        seatIds.mapNotNull { seatStorage[it] }

    override fun getSeat(id: Long): Seat? = seatStorage[id]

    fun saveSingleSeat() {
        seatStorage[1L] =
            Seat(
                id = 1,
                number = 1,
                price = 1000
            )
    }
}
