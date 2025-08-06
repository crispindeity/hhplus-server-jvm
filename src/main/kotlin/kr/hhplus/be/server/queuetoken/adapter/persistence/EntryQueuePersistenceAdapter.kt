package kr.hhplus.be.server.queuetoken.adapter.persistence

import java.util.UUID
import kr.hhplus.be.server.queuetoken.adapter.persistence.entity.QueueNumberEntity
import kr.hhplus.be.server.queuetoken.adapter.persistence.extensions.toDomain
import kr.hhplus.be.server.queuetoken.adapter.persistence.extensions.toEntity
import kr.hhplus.be.server.queuetoken.adapter.persistence.extensions.toUpdateEntity
import kr.hhplus.be.server.queuetoken.adapter.persistence.repository.EntryQueueNumberJpaRepository
import kr.hhplus.be.server.queuetoken.adapter.persistence.repository.EntryQueueRepository
import kr.hhplus.be.server.queuetoken.application.port.EntryQueuePort
import kr.hhplus.be.server.queuetoken.domain.QueueToken
import org.springframework.stereotype.Component

@Component
internal class EntryQueuePersistenceAdapter(
    private val repository: EntryQueueRepository,
    private val entryQueueNumberJpaRepository: EntryQueueNumberJpaRepository
) : EntryQueuePort {
    override fun getEntryQueueNextNumber(): Int = repository.findEntryQueueNextNumber()

    override fun saveEntryQueueToken(token: QueueToken) {
        repository.save(token.toEntity())
    }

    override fun existsWaitingQueueToken(userId: UUID): Boolean =
        repository.existsQueueTokenBy(
            userId = userId.toString(),
            status = QueueToken.Status.WAITING.toEntity()
        )

    override fun getCurrentAllowedQueueNumber(): Int = repository.findCurrentAllowedQueueNumber()

    override fun getEntryQueueToken(userId: UUID): QueueToken? =
        repository.findBy(userId.toString())?.toDomain()

    override fun update(token: QueueToken) {
        repository.update(token.toUpdateEntity())
    }

    override fun getQueueNumberByIdForUpdate(numberId: String): Int =
        entryQueueNumberJpaRepository.findByIdForUpdate(numberId)?.number ?: 1

    override fun incrementNextNumber(nextNumber: Int) {
        entryQueueNumberJpaRepository.save(QueueNumberEntity(number = nextNumber))
    }
}
