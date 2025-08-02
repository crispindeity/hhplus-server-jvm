package kr.hhplus.be.server.queuetoken.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import kr.hhplus.be.server.common.adapter.persistence.entity.BaseEntity

@Entity
@Table(name = "queue_tokens")
internal class QueueTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, length = 36)
    val userId: String,
    @Column(nullable = false)
    val queueNumber: Int,
    @Column(nullable = false, length = 1024)
    val token: String,
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val status: Status,
    @Column(nullable = false)
    val expiresAt: Instant
) : BaseEntity() {
    enum class Status {
        WAITING,
        COMPLETED,
        CANCELLED,
        EXPIRED
    }
}
