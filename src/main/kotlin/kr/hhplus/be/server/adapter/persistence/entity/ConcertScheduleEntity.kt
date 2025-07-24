package kr.hhplus.be.server.adapter.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "concert_schedules")
internal class ConcertScheduleEntity(
    @Column(nullable = false)
    val concertId: Long,
    @Column(nullable = false)
    val date: LocalDate
) : BaseEntity()
