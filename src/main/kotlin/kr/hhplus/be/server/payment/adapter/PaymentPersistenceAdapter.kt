package kr.hhplus.be.server.payment.adapter

import kr.hhplus.be.server.payment.adapter.persistence.extensions.toDomain
import kr.hhplus.be.server.payment.adapter.persistence.extensions.toEntity
import kr.hhplus.be.server.payment.adapter.persistence.extensions.toUpdateEntity
import kr.hhplus.be.server.payment.adapter.persistence.repository.PaymentRepository
import kr.hhplus.be.server.payment.application.port.PaymentPort
import kr.hhplus.be.server.payment.domain.Payment
import org.springframework.stereotype.Component

@Component
internal class PaymentPersistenceAdapter(
    private val repository: PaymentRepository
) : PaymentPort {
    override fun save(payment: Payment): Long = repository.save(payment.toEntity())

    override fun getAll(paymentIds: List<Long>): List<Payment> =
        repository.findAll(paymentIds).map { it.toDomain() }

    override fun update(payment: Payment) {
        repository.update(payment.toUpdateEntity())
    }

    override fun updateStatusToCancelled(ids: List<Long>) {
        repository.updateStatusToCancelled(ids)
    }
}
