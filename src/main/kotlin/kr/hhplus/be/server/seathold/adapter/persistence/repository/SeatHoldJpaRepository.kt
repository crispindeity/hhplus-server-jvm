package kr.hhplus.be.server.seathold.adapter.persistence.repository

import kr.hhplus.be.server.seathold.adapter.persistence.entity.SeatHoldEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface SeatHoldJpaRepository : JpaRepository<SeatHoldEntity, Long>
