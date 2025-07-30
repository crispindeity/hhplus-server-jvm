package kr.hhplus.be.server.payment.adapter.persistence.repository

import kr.hhplus.be.server.payment.adapter.persistence.entity.PaymentEntity

internal interface PaymentRepository {
    fun save(entity: PaymentEntity): Long

    fun findAll(ids: List<Long>): List<PaymentEntity>

    fun update(entity: PaymentEntity)

    fun updateStatusToCancelled(ids: List<Long>)
}
