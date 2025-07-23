package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.entity.QueueTokenJpaEntity
import org.springframework.stereotype.Repository

@Repository
internal class EntryQueueDomainRepository(
    private val jpaRepository: EntryQueueJpaRepository
) : EntryQueueRepository {
    override fun save(entity: QueueTokenJpaEntity) {
        jpaRepository.save(entity)
    }

    override fun existsQueueTokenBy(
        userId: String,
        status: QueueTokenJpaEntity.Status
    ): Boolean = jpaRepository.existsByUserIdAndStatus(userId, status)

    override fun findBy(userId: String): QueueTokenJpaEntity? = jpaRepository.findByUserId(userId)

    override fun findCurrentAllowedQueueNumber(): Int =
        jpaRepository.findCurrentAllowedQueueNumber()

    override fun findEntryQueueNextNumber(): Int = jpaRepository.findEntryQueueNextNumber()
}
