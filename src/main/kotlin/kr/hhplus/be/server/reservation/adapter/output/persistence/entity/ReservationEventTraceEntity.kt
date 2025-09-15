package kr.hhplus.be.server.reservation.adapter.output.persistence.entity

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
@Table(name = "reservation_event_traces")
internal class ReservationEventTraceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, length = 36)
    val eventId: String,
    @Column(nullable = false)
    val reservationId: Long,
    @Column(nullable = false)
    val occurredAt: LocalDateTime,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val eventType: EventType
) : BaseEntity() {
    enum class EventType {
        PAYMENT,
        SEAT_HELD,
        CONCERT_SEAT_HELD
    }
}
