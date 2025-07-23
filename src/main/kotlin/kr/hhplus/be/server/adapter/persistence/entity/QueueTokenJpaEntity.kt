package kr.hhplus.be.server.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "queue_tokens")
internal class QueueTokenJpaEntity(
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
