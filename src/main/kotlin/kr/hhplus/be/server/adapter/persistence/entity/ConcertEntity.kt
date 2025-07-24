package kr.hhplus.be.server.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "concerts")
internal class ConcertEntity(
    @Column(nullable = false, length = 255)
    val title: String
) : BaseEntity()
