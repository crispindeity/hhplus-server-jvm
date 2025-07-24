package kr.hhplus.be.server.adapter.persistence

import kr.hhplus.be.server.adapter.persistence.extensions.toDto
import kr.hhplus.be.server.adapter.persistence.repository.ConcertSeatRepository
import kr.hhplus.be.server.application.port.ConcertSeatPort
import kr.hhplus.be.server.application.service.dto.AvailableSeatDto
import org.springframework.stereotype.Component

@Component
internal class ConcertSeatPersistenceAdapter(
    private val repository: ConcertSeatRepository
) : ConcertSeatPort {
    override fun getAvailableSeats(scheduleId: Long): List<AvailableSeatDto> =
        repository.findAvailableSeats(scheduleId).map { it.toDto() }
}
