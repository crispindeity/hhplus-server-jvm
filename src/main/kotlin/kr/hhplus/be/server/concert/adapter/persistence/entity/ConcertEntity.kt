package kr.hhplus.be.server.concert.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.hhplus.be.server.common.adapter.persistence.entity.BaseEntity

@Entity
@Table(name = "concerts")
internal class ConcertEntity(
    @Column(nullable = false, length = 255)
    val title: String
) : BaseEntity()
