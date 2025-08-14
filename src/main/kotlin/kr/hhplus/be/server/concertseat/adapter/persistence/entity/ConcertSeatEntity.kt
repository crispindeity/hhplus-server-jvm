package kr.hhplus.be.server.concertseat.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.hhplus.be.server.common.adapter.persistence.entity.BaseEntity

@Entity
@Table(name = "concert_seats")
internal class ConcertSeatEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
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
