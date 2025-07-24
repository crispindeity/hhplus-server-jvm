package kr.hhplus.be.server.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "concert_seats")
internal class ConcertSeatEntity(
    @Column(nullable = false)
    val scheduleId: Long,
    @Column(nullable = false)
    val seatId: Long,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: Status
) : BaseEntity() {
    enum class Status {
        HELD,
        AVAILABLE,
        RESERVED
    }
}
