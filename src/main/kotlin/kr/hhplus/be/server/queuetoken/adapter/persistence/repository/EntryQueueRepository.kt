package kr.hhplus.be.server.queuetoken.adapter.persistence.repository

import kr.hhplus.be.server.queuetoken.adapter.persistence.entity.QueueTokenEntity

internal interface EntryQueueRepository {
    fun save(entity: QueueTokenEntity)

    fun existsQueueTokenBy(
        userId: String,
        status: QueueTokenEntity.Status
    ): Boolean

    fun findBy(userId: String): QueueTokenEntity?

    fun findCurrentAllowedQueueNumber(): Int

    fun findEntryQueueNextNumber(): Int

    fun update(entity: QueueTokenEntity)
}
