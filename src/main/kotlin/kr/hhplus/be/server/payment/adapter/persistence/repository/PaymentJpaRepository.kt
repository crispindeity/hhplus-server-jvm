package kr.hhplus.be.server.payment.adapter.persistence.repository

import kr.hhplus.be.server.payment.adapter.persistence.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface PaymentJpaRepository : JpaRepository<PaymentEntity, Long> {
    fun findAllByIdInAndStatus(
        ids: List<Long>,
        status: PaymentEntity.Status = PaymentEntity.Status.PENDING
    ): List<PaymentEntity>
}
