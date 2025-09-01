package kr.hhplus.be.server.fake

import java.util.UUID
import kr.hhplus.be.server.reservation.application.port.ReservationEventTracePort
import kr.hhplus.be.server.reservation.domain.ReservationEventTrace

internal class FakeReservationEventPort : ReservationEventTracePort {
    private val storage: MutableMap<Long, ReservationEventTrace> = mutableMapOf()
    private var sequence = 0L

    override fun save(reservationEventTrace: ReservationEventTrace) {
        if (reservationEventTrace.id == 0L || storage[reservationEventTrace.id] == null) {
            val newReservationEventTrace: ReservationEventTrace =
                reservationEventTrace.copy(id = ++sequence)
            storage[newReservationEventTrace.id] = newReservationEventTrace
        } else {
            storage[reservationEventTrace.id] = reservationEventTrace
        }
    }

    override fun count(eventId: UUID): Long =
        storage.values
            .filter { it.eventId == eventId }
            .size
            .toLong()
}
