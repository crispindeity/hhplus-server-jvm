package kr.hhplus.be.server.application.port

import kr.hhplus.be.server.application.service.dto.AvailableSeatDto

internal interface ConcertSeatPort {
    fun getAvailableSeats(scheduleId: Long): List<AvailableSeatDto>
}
