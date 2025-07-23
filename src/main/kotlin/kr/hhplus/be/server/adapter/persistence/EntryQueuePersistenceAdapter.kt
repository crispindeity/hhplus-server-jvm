package kr.hhplus.be.server.adapter.persistence

import java.util.UUID
import kr.hhplus.be.server.adapter.persistence.extensions.toDomain
import kr.hhplus.be.server.adapter.persistence.extensions.toEntity
import kr.hhplus.be.server.adapter.persistence.repository.EntryQueueRepository
import kr.hhplus.be.server.application.port.EntryQueuePort
import kr.hhplus.be.server.domain.QueueToken
import org.springframework.stereotype.Component

@Component
internal class EntryQueuePersistenceAdapter(
    private val repository: EntryQueueRepository
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
}
