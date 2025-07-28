package kr.hhplus.be.server.seat.adapter.persistence.repository

import kr.hhplus.be.server.seat.adapter.persistence.entity.SeatEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface SeatJpaRepository : JpaRepository<SeatEntity, Long> {
    fun findAllByIdIn(seatIds: List<Long>): List<SeatEntity>
}
