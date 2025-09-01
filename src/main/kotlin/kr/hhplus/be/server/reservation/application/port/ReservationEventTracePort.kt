package kr.hhplus.be.server.reservation.application.port

import java.util.UUID
import kr.hhplus.be.server.reservation.domain.ReservationEventTrace

internal interface ReservationEventTracePort {
    fun save(reservationEventTrace: ReservationEventTrace)

    fun count(eventId: UUID): Long
}
