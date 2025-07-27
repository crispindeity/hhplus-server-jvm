package kr.hhplus.be.server.adapter.persistence

import kr.hhplus.be.server.adapter.persistence.extensions.toDomain
import kr.hhplus.be.server.adapter.persistence.extensions.toDto
import kr.hhplus.be.server.adapter.persistence.extensions.toEntity
import kr.hhplus.be.server.adapter.persistence.repository.ConcertSeatRepository
import kr.hhplus.be.server.application.port.ConcertSeatPort
import kr.hhplus.be.server.application.service.dto.AvailableSeatDto
import kr.hhplus.be.server.domain.ConcertSeat
import org.springframework.stereotype.Component

@Component
internal class ConcertSeatPersistenceAdapter(
    private val repository: ConcertSeatRepository
) : ConcertSeatPort {
    override fun getAvailableSeats(scheduleId: Long): List<AvailableSeatDto> =
        repository.findAvailableSeats(scheduleId).map { it.toDto() }

    override fun getConcertSeat(concertSeatId: Long): ConcertSeat? =
        repository.findConcertSeat(concertSeatId)?.toDomain()

    override fun update(concertSeat: ConcertSeat) {
        repository.update(concertSeat.toEntity())
    }
}
