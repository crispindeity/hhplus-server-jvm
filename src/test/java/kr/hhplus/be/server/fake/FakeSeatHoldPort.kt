package kr.hhplus.be.server.fake

import kr.hhplus.be.server.application.port.SeatHoldPort
import kr.hhplus.be.server.domain.SeatHold

internal class FakeSeatHoldPort : SeatHoldPort {
    private val storage: MutableMap<Long, SeatHold> = mutableMapOf()
    private var sequence = 0L

    override fun save(seatHold: SeatHold) {
        if (seatHold.id == 0L || storage[seatHold.id] == null) {
            val newSeatHold: SeatHold = seatHold.copy(id = sequence++)
            storage[newSeatHold.id] = newSeatHold
        } else {
            storage[seatHold.id] = seatHold
        }
    }
}
