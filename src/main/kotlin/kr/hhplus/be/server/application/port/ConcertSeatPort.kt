package kr.hhplus.be.server.application.port

import kr.hhplus.be.server.application.service.dto.AvailableSeatDto
import kr.hhplus.be.server.domain.ConcertSeat

internal interface ConcertSeatPort {
    fun getAvailableSeats(scheduleId: Long): List<AvailableSeatDto>

    fun getConcertSeat(concertSeatId: Long): ConcertSeat?

    fun update(concertSeat: ConcertSeat)
}
