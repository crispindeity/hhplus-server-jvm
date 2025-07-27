package kr.hhplus.be.server.seathold.adapter

import kr.hhplus.be.server.seathold.adapter.persistence.extensions.toEntity
import kr.hhplus.be.server.seathold.adapter.persistence.repository.SeatHoldRepository
import kr.hhplus.be.server.seathold.application.port.SeatHoldPort
import kr.hhplus.be.server.seathold.domain.SeatHold
import org.springframework.stereotype.Component

@Component
internal class SeatHoldPersistenceAdapter(
    private val repository: SeatHoldRepository
) : SeatHoldPort {
    override fun save(seatHold: SeatHold) {
        repository.save(seatHold.toEntity())
    }

    override fun deleteAll(seatIds: List<Long>) {
        repository.deleteAll(seatIds)
    }
}
