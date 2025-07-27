package kr.hhplus.be.server.queuetoken.adapter.persistence.repository

import jakarta.persistence.EntityManager
import kr.hhplus.be.server.queuetoken.adapter.persistence.entity.QueueTokenEntity
import org.springframework.stereotype.Repository

@Repository
internal class EntryQueueDomainRepository(
    private val entityManager: EntityManager,
    private val jpaRepository: EntryQueueJpaRepository
) : EntryQueueRepository {
    override fun save(entity: QueueTokenEntity) {
        jpaRepository.save(entity)
    }

    override fun existsQueueTokenBy(
        userId: String,
        status: QueueTokenEntity.Status
    ): Boolean = jpaRepository.existsByUserIdAndStatus(userId, status)

    override fun findBy(userId: String): QueueTokenEntity? = jpaRepository.findByUserId(userId)

    override fun findCurrentAllowedQueueNumber(): Int =
        jpaRepository.findCurrentAllowedQueueNumber()

    override fun findEntryQueueNextNumber(): Int = jpaRepository.findEntryQueueNextNumber()

    override fun update(entity: QueueTokenEntity) {
        entityManager.merge(entity)
    }
}
