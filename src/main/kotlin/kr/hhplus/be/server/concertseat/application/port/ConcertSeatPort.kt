package kr.hhplus.be.server.concertseat.application.port

import kr.hhplus.be.server.concert.application.service.dto.AvailableSeatDto
import kr.hhplus.be.server.concertseat.domain.ConcertSeat

internal interface ConcertSeatPort {
    fun getAvailableSeats(scheduleId: Long): List<AvailableSeatDto>

    fun getConcertSeat(concertSeatId: Long): ConcertSeat?

    fun update(concertSeat: ConcertSeat)

    fun updateStatusToAvailable(ids: List<Long>)
}
