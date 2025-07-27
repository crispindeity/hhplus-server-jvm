package kr.hhplus.be.server.reservation.adapter.persistence.repository

import kr.hhplus.be.server.reservation.adapter.persistence.entity.ReservationEntity

internal interface ReservationRepository {
    fun save(entity: ReservationEntity)

    fun update(entity: ReservationEntity)
}
