package kr.hhplus.be.server.payment.adapter.persistence.extensions

import java.util.UUID
import kr.hhplus.be.server.payment.adapter.persistence.entity.PaymentEntity
import kr.hhplus.be.server.payment.domain.Payment

internal fun Payment.toEntity(): PaymentEntity =
    PaymentEntity(
        userId = this.userId.toString(),
        price = this.price,
        status =
            when (this.status) {
                Payment.Status.PENDING -> PaymentEntity.Status.PENDING
                Payment.Status.COMPLETED -> PaymentEntity.Status.COMPLETED
                Payment.Status.CANCELLED -> PaymentEntity.Status.CANCELLED
            }
    )

internal fun PaymentEntity.toDomain(): Payment =
    Payment(
        id = this.id!!,
        userId = UUID.fromString(this.userId),
        price = this.price,
        paidAt = this.paidAt,
        status =
            when (this.status) {
                PaymentEntity.Status.PENDING -> Payment.Status.PENDING
                PaymentEntity.Status.COMPLETED -> Payment.Status.COMPLETED
                PaymentEntity.Status.CANCELLED -> Payment.Status.CANCELLED
            }
    )

internal fun Payment.toUpdateEntity(): PaymentEntity =
    PaymentEntity(
        id = this.id,
        userId = this.userId.toString(),
        price = this.price,
        paidAt = this.paidAt,
        status =
            when (this.status) {
                Payment.Status.PENDING -> PaymentEntity.Status.PENDING
                Payment.Status.COMPLETED -> PaymentEntity.Status.COMPLETED
                Payment.Status.CANCELLED -> PaymentEntity.Status.CANCELLED
            }
    )
