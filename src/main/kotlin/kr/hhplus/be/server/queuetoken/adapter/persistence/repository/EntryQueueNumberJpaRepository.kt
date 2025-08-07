package kr.hhplus.be.server.queuetoken.adapter.persistence.repository

import jakarta.persistence.LockModeType
import kr.hhplus.be.server.queuetoken.adapter.persistence.entity.QueueNumberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

internal interface EntryQueueNumberJpaRepository : JpaRepository<QueueNumberEntity, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM QueueNumberEntity q WHERE q.id = :id")
    fun findByIdForUpdate(
        @Param("id") id: String
    ): QueueNumberEntity?
}
