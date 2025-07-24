package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.entity.ConcertEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface ConcertJpaRepository : JpaRepository<ConcertEntity, Long>
