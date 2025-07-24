package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.dto.AvailableSeatProjection
import kr.hhplus.be.server.adapter.persistence.entity.ConcertSeatEntity

internal interface ConcertSeatRepository {
    fun findAvailableSeats(scheduleId: Long): List<AvailableSeatProjection>

    fun findConcertSeat(concertSeatId: Long): ConcertSeatEntity?

    fun update(entity: ConcertSeatEntity)
}
