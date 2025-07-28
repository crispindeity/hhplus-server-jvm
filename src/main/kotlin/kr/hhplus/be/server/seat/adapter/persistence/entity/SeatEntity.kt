package kr.hhplus.be.server.seat.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.hhplus.be.server.common.adapter.persistence.entity.BaseEntity

@Entity
@Table(name = "seats")
internal class SeatEntity(
    @Column(nullable = false)
    val number: Long,
    @Column(nullable = false)
    val price: Long
) : BaseEntity()
