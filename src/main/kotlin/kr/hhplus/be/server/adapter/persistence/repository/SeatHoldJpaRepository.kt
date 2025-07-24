package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.entity.SeatHoldEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface SeatHoldJpaRepository : JpaRepository<SeatHoldEntity, Long>
