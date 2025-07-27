package kr.hhplus.be.server.adapter.persistence

import kr.hhplus.be.server.adapter.persistence.extensions.toEntity
import kr.hhplus.be.server.adapter.persistence.repository.SeatHoldRepository
import kr.hhplus.be.server.application.port.SeatHoldPort
import kr.hhplus.be.server.domain.SeatHold
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
