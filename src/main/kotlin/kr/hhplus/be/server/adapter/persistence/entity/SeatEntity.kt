package kr.hhplus.be.server.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "seats")
internal class SeatEntity(
    @Column(nullable = false)
    val number: Long,
    @Column(nullable = false)
    val price: Long
) : BaseEntity()
