package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.entity.QueueTokenJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

internal interface EntryQueueJpaRepository : JpaRepository<QueueTokenJpaEntity, Long> {
    fun existsByUserIdAndStatus(
        userId: String,
        status: QueueTokenJpaEntity.Status
    ): Boolean

    fun findByUserId(userId: String): QueueTokenJpaEntity?

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
