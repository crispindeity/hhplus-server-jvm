package kr.hhplus.be.server.queuetoken.application.port

import java.util.UUID
import kr.hhplus.be.server.queuetoken.domain.QueueToken

internal interface EntryQueuePort {
    fun getEntryQueueNextNumber(): Int

    fun saveEntryQueueToken(token: QueueToken)

    fun existsWaitingQueueToken(userId: UUID): Boolean

    fun getCurrentAllowedQueueNumber(): Int

    fun getEntryQueueToken(userId: UUID): QueueToken?

    fun update(token: QueueToken)

    fun getQueueNumberByIdForUpdate(numberId: String): Int

    fun incrementNextNumber(nextNumber: Int)
}
