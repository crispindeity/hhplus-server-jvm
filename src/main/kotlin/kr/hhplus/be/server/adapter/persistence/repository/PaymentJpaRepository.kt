package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface PaymentJpaRepository : JpaRepository<PaymentEntity, Long> {
    fun findAllByIdIn(ids: List<Long>): List<PaymentEntity>
}
