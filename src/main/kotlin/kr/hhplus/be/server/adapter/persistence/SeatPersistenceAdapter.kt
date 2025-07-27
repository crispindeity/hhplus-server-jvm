package kr.hhplus.be.server.adapter.persistence

import kr.hhplus.be.server.adapter.persistence.extensions.toDomain
import kr.hhplus.be.server.adapter.persistence.repository.SeatRepository
import kr.hhplus.be.server.application.port.SeatPort
import kr.hhplus.be.server.domain.Seat
import org.springframework.stereotype.Component

@Component
internal class SeatPersistenceAdapter(
    private val repository: SeatRepository
) : SeatPort {
    override fun getAllSeat(seatIds: List<Long>): List<Seat> =
        repository.findAllBy(seatIds).map { it.toDomain() }

    override fun getSeat(id: Long): Seat? = repository.findBy(id)?.toDomain()
}
