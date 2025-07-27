package kr.hhplus.be.server.adapter.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "payments")
internal class PaymentEntity(
    override val id: Long? = null,
    val userId: String,
    val status: Status,
    val price: Long,
    val paidAt: LocalDateTime? = null
) : BaseEntity() {
    enum class Status {
        PENDING,
        COMPLETED,
        CANCELLED
    }
}
