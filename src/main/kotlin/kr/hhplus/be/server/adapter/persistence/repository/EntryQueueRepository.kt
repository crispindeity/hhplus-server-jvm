package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.entity.QueueTokenJpaEntity

internal interface EntryQueueRepository {
    fun save(entity: QueueTokenJpaEntity)

    fun existsQueueTokenBy(
        userId: String,
        status: QueueTokenJpaEntity.Status
    ): Boolean

    fun findBy(userId: String): QueueTokenJpaEntity?

    fun findCurrentAllowedQueueNumber(): Int

    fun findEntryQueueNextNumber(): Int
}
