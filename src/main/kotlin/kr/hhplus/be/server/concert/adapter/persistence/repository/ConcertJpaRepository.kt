package kr.hhplus.be.server.concert.adapter.persistence.repository

import kr.hhplus.be.server.concert.adapter.persistence.entity.ConcertEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface ConcertJpaRepository : JpaRepository<ConcertEntity, Long>
