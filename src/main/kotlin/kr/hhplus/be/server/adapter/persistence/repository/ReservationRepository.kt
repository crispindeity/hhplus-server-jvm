package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.entity.ReservationEntity

internal interface ReservationRepository {
    fun save(entity: ReservationEntity)
}
