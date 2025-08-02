package kr.hhplus.be.server.fake

import kr.hhplus.be.server.seathold.application.port.SeatHoldPort
import kr.hhplus.be.server.seathold.domain.SeatHold

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

    override fun deleteAll(seatIds: List<Long>) {
        seatIds.forEach { seatId ->
            val toRemove: SeatHold? = storage.values.find { it.concertSeatId == seatId }
            if (toRemove != null) {
                storage.remove(toRemove.id)
            }
        }
    }

    override fun deleteAllByConcertSeatIds(concertSeatIds: List<Long>) {
        concertSeatIds
            .mapNotNull { id ->
                storage.values.find { it.concertSeatId == id }
            }.forEach { reservation ->
                storage.remove(reservation.id)
            }
    }
}
