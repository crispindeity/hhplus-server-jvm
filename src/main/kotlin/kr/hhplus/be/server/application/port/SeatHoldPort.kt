package kr.hhplus.be.server.application.port

import kr.hhplus.be.server.domain.SeatHold

internal interface SeatHoldPort {
    fun save(seatHold: SeatHold)

    fun deleteAll(seatIds: List<Long>)
}
