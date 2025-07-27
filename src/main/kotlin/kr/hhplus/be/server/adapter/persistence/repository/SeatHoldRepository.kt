package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.entity.SeatHoldEntity

internal interface SeatHoldRepository {
    fun save(entity: SeatHoldEntity)

    fun deleteAll(ids: List<Long>)
}
