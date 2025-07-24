package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.dto.AvailableSeatProjection

internal interface ConcertSeatRepository {
    fun findAvailableSeats(scheduleId: Long): List<AvailableSeatProjection>
}
