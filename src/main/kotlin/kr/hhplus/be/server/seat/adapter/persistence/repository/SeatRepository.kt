package kr.hhplus.be.server.seat.adapter.persistence.repository

import kr.hhplus.be.server.seat.adapter.persistence.entity.SeatEntity

internal interface SeatRepository {
    fun findBy(id: Long): SeatEntity?

    fun findAllBy(seatIds: List<Long>): List<SeatEntity>
}
