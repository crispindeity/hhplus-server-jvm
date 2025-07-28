package kr.hhplus.be.server.queuetoken.adapter.persistence.repository

import kr.hhplus.be.server.queuetoken.adapter.persistence.entity.QueueTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

internal interface EntryQueueJpaRepository : JpaRepository<QueueTokenEntity, Long> {
    fun existsByUserIdAndStatus(
        userId: String,
        status: QueueTokenEntity.Status
    ): Boolean

    fun findByUserId(userId: String): QueueTokenEntity?

    @Query(
        value = """
            SELECT MAX(queue_number)
            FROM (
                SELECT queue_number
                FROM queue_tokens
                WHERE status = 'WAITING'
                  AND expires_at > NOW()
                ORDER BY queue_number ASC
                LIMIT 10
            ) AS allowed
        """,
        nativeQuery = true
    )
    fun findCurrentAllowedQueueNumber(): Int

    @Query(
        value = """
            SELECT COALESCE(MAX(queue_number), 0) + 1
            FROM queue_tokens
        """,
        nativeQuery = true
    )
    fun findEntryQueueNextNumber(): Int
}
