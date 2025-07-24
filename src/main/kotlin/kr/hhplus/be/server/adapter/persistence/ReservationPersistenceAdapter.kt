package kr.hhplus.be.server.adapter.persistence

import kr.hhplus.be.server.adapter.persistence.extensions.toEntity
import kr.hhplus.be.server.adapter.persistence.repository.ReservationRepository
import kr.hhplus.be.server.application.port.ReservationPort
import kr.hhplus.be.server.domain.Reservation
import org.springframework.stereotype.Component

@Component
internal class ReservationPersistenceAdapter(
    private val repository: ReservationRepository
) : ReservationPort {
    override fun save(reservation: Reservation) {
        repository.save(reservation.toEntity())
    }
}
