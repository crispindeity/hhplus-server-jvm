package kr.hhplus.be.server.application.service

import java.util.UUID
import kr.hhplus.be.server.application.port.EntryQueuePort
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.exception.QueueTokenException
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.domain.QueueToken
import org.slf4j.Logger
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
internal class QueueAccessValidator(
    private val entryQueuePort: EntryQueuePort
) {
    private val logger: Logger = Log.getLogger(QueueAccessValidator::class.java)

    fun validateQueueToken(
        userId: UUID,
        queueNumber: Int
    ) = Log.logging(logger) { log ->
        log["method"] = "validateQueueToken()"
        val entryQueueToken: QueueToken =
            entryQueuePort.getEntryQueueToken(userId)
                ?: throw QueueTokenException(ErrorCode.NOT_FOUND_QUEUE_TOKEN)

        if (entryQueueToken.status != QueueToken.Status.WAITING) {
            throw QueueTokenException(ErrorCode.QUEUE_TOKEN_INVALID_STATUS)
        }

        if (entryQueueToken.queueNumber != queueNumber) {
            throw QueueTokenException(ErrorCode.INVALID_QUEUE_TOKEN)
        }

        val currentAllowed: Int = entryQueuePort.getCurrentAllowedQueueNumber()

        if (queueNumber > currentAllowed) {
            throw QueueTokenException(ErrorCode.QUEUE_NOT_YET_ALLOWED)
        }
    }
}
