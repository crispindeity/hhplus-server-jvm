package kr.hhplus.be.server.seathold.application.port

import kr.hhplus.be.server.seathold.domain.SeatHold

internal interface SeatHoldPort {
    fun save(seatHold: SeatHold)

    fun deleteAll(seatIds: List<Long>)

    fun deleteAllByConcertSeatIds(concertSeatIds: List<Long>)
}
