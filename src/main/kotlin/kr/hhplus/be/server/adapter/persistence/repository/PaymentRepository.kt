package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.entity.PaymentEntity

internal interface PaymentRepository {
    fun save(entity: PaymentEntity): Long

    fun findAll(ids: List<Long>): List<PaymentEntity>

    fun update(entity: PaymentEntity)
}
