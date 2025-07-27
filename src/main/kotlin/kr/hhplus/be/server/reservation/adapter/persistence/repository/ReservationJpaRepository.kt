package kr.hhplus.be.server.reservation.adapter.persistence.repository

import kr.hhplus.be.server.reservation.adapter.persistence.entity.ReservationEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface ReservationJpaRepository : JpaRepository<ReservationEntity, Long>
