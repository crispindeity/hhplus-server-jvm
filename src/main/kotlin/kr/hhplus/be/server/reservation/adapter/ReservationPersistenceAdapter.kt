package kr.hhplus.be.server.reservation.adapter

import java.time.LocalDateTime
import kr.hhplus.be.server.reservation.adapter.persistence.extensions.toDomain
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
    override fun save(reservation: Reservation): Long = repository.save(reservation.toEntity())

    override fun getAll(userId: String): List<Reservation> =
        repository.findAll(userId).map { it.toDomain() }

    override fun update(reservation: Reservation) {
        repository.update(reservation.toUpdateEntity())
    }

    override fun findAllByRangeAndInProgress(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Reservation> = repository.findAllByRangeAndInProgress(start, end).map { it.toDomain() }

    override fun updateStatusToExpired(ids: List<Long>) {
        repository.updateStatusToExpired(ids)
    }

    override fun getReservation(reservationId: Long): Reservation? =
        repository.findBy(reservationId)?.toDomain()
}
