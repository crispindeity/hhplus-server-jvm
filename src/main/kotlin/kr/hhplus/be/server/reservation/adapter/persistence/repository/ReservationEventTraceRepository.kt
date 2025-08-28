package kr.hhplus.be.server.reservation.adapter.persistence.repository

import kr.hhplus.be.server.reservation.adapter.persistence.entity.ReservationEventTraceEntity

internal interface ReservationEventTraceRepository {
    fun save(entity: ReservationEventTraceEntity)
}
