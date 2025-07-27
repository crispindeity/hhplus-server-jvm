package kr.hhplus.be.server.reservation.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDateTime
import kr.hhplus.be.server.common.adapter.persistence.entity.BaseEntity

@Entity
@Table(name = "reservations")
internal class ReservationEntity(
    override val id: Long? = null,
    @Column(nullable = false, length = 36)
    val userId: String,
    @Column(nullable = false)
    val concertId: Long,
    val paymentId: Long? = null,
    @Column(nullable = false)
    val concertSeatId: Long,
    val confirmedAt: LocalDateTime? = null,
    @Column(nullable = false)
    val reservedAt: LocalDateTime,
    @Column(nullable = false)
    val expiresAt: LocalDateTime,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: Status
) : BaseEntity() {
    enum class Status {
        IN_PROGRESS,
        CANCELLED,
        CONFIRMED,
        EXPIRED
    }
}
