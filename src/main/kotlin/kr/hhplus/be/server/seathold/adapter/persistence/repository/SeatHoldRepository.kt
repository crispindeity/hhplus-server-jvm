package kr.hhplus.be.server.seathold.adapter.persistence.repository

import kr.hhplus.be.server.seathold.adapter.persistence.entity.SeatHoldEntity

internal interface SeatHoldRepository {
    fun save(entity: SeatHoldEntity)

    fun deleteAll(ids: List<Long>)

    fun deleteAllByConcertSeatIds(concertSeatIds: List<Long>)
}
