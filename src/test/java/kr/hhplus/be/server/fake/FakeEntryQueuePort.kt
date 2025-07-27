package kr.hhplus.be.server.fake

import java.util.UUID
import kr.hhplus.be.server.queuetoken.application.port.EntryQueuePort
import kr.hhplus.be.server.queuetoken.domain.QueueToken

class FakeEntryQueuePort : EntryQueuePort {
    private val storage: MutableMap<Long, QueueToken> = mutableMapOf()
    private var sequence: Long = 0L

    override fun saveEntryQueueToken(token: QueueToken) {
        if (token.id == 0L || storage[token.id] == null) {
            val newToken: QueueToken = token.copy(id = sequence++)
            storage[newToken.id] = newToken
        } else {
            storage[token.id] = token
        }
    }

    override fun existsWaitingQueueToken(userId: UUID): Boolean =
        storage.values.any {
            it.userId == userId &&
                it.status == QueueToken.Status.WAITING
        }

    override fun getCurrentAllowedQueueNumber(): Int =
        storage.values
            .sortedBy { it.queueNumber }
            .take(10)
            .maxOf { it.queueNumber }

    override fun getEntryQueueToken(userId: UUID): QueueToken? =
        storage.values.find { it.userId == userId }

    override fun update(token: QueueToken) {
        storage[token.id] = token
    }

    override fun getEntryQueueNextNumber(): Int = storage.count().plus(1)

    fun saveSingleQueueToken(userId: UUID) {
        storage[1L] =
            QueueToken(
                id = 1L,
                userId = userId,
                queueNumber = 1,
                token = "token"
            )
    }

    fun saveHundredQueueToken(): MutableMap<Long, QueueToken> {
        repeat(100) { index ->
            val sequence: Long = index.toLong() + 1
            storage[sequence] =
                QueueToken(
                    id = sequence,
                    userId = UUID.randomUUID(),
                    queueNumber = sequence.toInt(),
                    token = "token"
                )
        }
        return storage
    }
}
