package kr.hhplus.be.server.application.service

import java.util.UUID
import kr.hhplus.be.server.application.port.EntryQueuePort
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.domain.QueueToken
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
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
                ?: throw CustomException(ErrorCode.NOT_FOUND_QUEUE_TOKEN)

        if (entryQueueToken.status != QueueToken.Status.WAITING) {
            throw CustomException(ErrorCode.QUEUE_TOKEN_INVALID_STATUS)
        }

        if (entryQueueToken.queueNumber != queueNumber) {
            throw CustomException(ErrorCode.INVALID_QUEUE_TOKEN)
        }

        val currentAllowed: Int = entryQueuePort.getCurrentAllowedQueueNumber()

        if (queueNumber > currentAllowed) {
            throw CustomException(ErrorCode.QUEUE_NOT_YET_ALLOWED)
        }
    }
}
