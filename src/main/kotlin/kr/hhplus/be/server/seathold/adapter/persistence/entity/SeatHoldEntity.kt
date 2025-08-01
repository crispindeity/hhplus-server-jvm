package kr.hhplus.be.server.seathold.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "seat_holds")
internal class SeatHoldEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, unique = true)
    val concertSeatId: Long,
    @Column(nullable = false)
    val userId: String,
    @Column(nullable = false)
    val heldAt: LocalDateTime,
    @Column(nullable = false)
    val expiresAt: LocalDateTime
)
