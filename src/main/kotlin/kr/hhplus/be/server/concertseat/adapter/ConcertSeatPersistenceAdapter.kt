package kr.hhplus.be.server.concertseat.adapter

import kr.hhplus.be.server.concert.application.service.dto.AvailableSeatDto
import kr.hhplus.be.server.concertseat.adapter.persistence.extensions.toDomain
import kr.hhplus.be.server.concertseat.adapter.persistence.extensions.toDto
import kr.hhplus.be.server.concertseat.adapter.persistence.extensions.toEntity
import kr.hhplus.be.server.concertseat.adapter.persistence.repository.ConcertSeatRepository
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.concertseat.domain.ConcertSeat
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

    override fun updateStatusToAvailable(ids: List<Long>) {
        repository.updateStatusToAvailable(ids)
    }
}
