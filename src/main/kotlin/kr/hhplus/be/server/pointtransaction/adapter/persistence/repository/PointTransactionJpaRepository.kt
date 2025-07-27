package kr.hhplus.be.server.pointtransaction.adapter.persistence.repository

import kr.hhplus.be.server.pointtransaction.adapter.persistence.entity.PointTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface PointTransactionJpaRepository : JpaRepository<PointTransactionEntity, Long>
