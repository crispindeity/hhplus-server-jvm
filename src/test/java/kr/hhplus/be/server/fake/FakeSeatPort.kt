package kr.hhplus.be.server.fake

import kr.hhplus.be.server.application.port.SeatPort
import kr.hhplus.be.server.domain.Seat

internal class FakeSeatPort : SeatPort {
    private val seatStorage = mutableMapOf<Long, Seat>()

    override fun getAllSeat(seatIds: List<Long>): List<Seat> =
        seatIds.mapNotNull { seatStorage[it] }

    fun saveSingleSeat() {
        seatStorage[1L] =
            Seat(
                id = 1,
                number = 1,
                price = 1000
            )
    }
}
