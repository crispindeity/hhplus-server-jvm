package kr.hhplus.be.server.reservation.adapter

import kr.hhplus.be.server.reservation.adapter.persistence.extensions.toEntity
import kr.hhplus.be.server.reservation.adapter.persistence.extensions.toUpdateEntity
import kr.hhplus.be.server.reservation.adapter.persistence.repository.ReservationRepository
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.reservation.domain.Reservation
import org.springframework.stereotype.Component

@Component
internal class ReservationPersistenceAdapter(
    private val repository: ReservationRepository
) : ReservationPort {
    override fun save(reservation: Reservation) {
        repository.save(reservation.toEntity())
    }

    override fun getAll(userId: String): List<Reservation> {
        TODO()
    }

    override fun update(reservation: Reservation) {
        repository.update(reservation.toUpdateEntity())
    }
}
