package kr.hhplus.be.server.payment.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import kr.hhplus.be.server.common.adapter.persistence.entity.BaseEntity

@Entity
@Table(name = "payments")
internal class PaymentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val userId: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: Status,
    @Column(nullable = false)
    val price: Long,
    val paidAt: LocalDateTime? = null
) : BaseEntity() {
    enum class Status {
        PENDING,
        COMPLETED,
        CANCELLED
    }
}
