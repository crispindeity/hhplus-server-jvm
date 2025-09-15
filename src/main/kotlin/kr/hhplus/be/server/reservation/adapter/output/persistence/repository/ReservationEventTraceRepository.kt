package kr.hhplus.be.server.reservation.adapter.output.persistence.repository

import kr.hhplus.be.server.reservation.adapter.output.persistence.entity.ReservationEventTraceEntity

internal interface ReservationEventTraceRepository {
    fun save(entity: ReservationEventTraceEntity)

    fun count(eventId: String): Long
}
