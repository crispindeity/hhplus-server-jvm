package kr.hhplus.be.server.application.service

import java.util.UUID
import kr.hhplus.be.server.application.port.EntryQueuePort
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.domain.QueueToken
import org.slf4j.Logger

class EntryQueueService(
    private val entryQueuePort: EntryQueuePort,
    private val jwtHelper: JWTHelper
) {
    private val logger: Logger = Log.getLogger(EntryQueueService::class.java)

    fun createEntryQueueToken(userId: UUID): String =
        Log.logging(logger) { log ->
            log["method"] = "createEntryQueueToken()"
            if (entryQueuePort.existsWaitingQueueToken(userId)) {
                throw CustomException(ErrorCode.TOKEN_ALREADY_ISSUED, userId.toString())
            }
            val queueNumber: Int = entryQueuePort.getEntryQueueNextNumber()
            val entryQueueToken: String = jwtHelper.createJWT(userId, queueNumber)
            val queueToken =
                QueueToken(
                    userId = userId,
                    queueNumber = queueNumber,
                    token = entryQueueToken
                )
            entryQueuePort.saveEntryQueueToken(queueToken)
            entryQueueToken
        }
}
