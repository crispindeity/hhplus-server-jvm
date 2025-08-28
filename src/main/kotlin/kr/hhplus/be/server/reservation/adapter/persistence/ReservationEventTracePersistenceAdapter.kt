package kr.hhplus.be.server.reservation.adapter.persistence

import kr.hhplus.be.server.reservation.adapter.persistence.extensions.toEntity
import kr.hhplus.be.server.reservation.adapter.persistence.repository.ReservationEventTraceRepository
import kr.hhplus.be.server.reservation.application.port.ReservationEventTracePort
import kr.hhplus.be.server.reservation.domain.ReservationEventTrace
import org.springframework.stereotype.Component

@Component
internal class ReservationEventTracePersistenceAdapter(
    private val repository: ReservationEventTraceRepository
) : ReservationEventTracePort {
    override fun save(reservationEventTrace: ReservationEventTrace) {
        repository.save(reservationEventTrace.toEntity())
    }
}
