package kr.hhplus.be.server.queuetoken.application.service

import java.util.UUID
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.queuetoken.application.port.EntryQueuePort
import kr.hhplus.be.server.queuetoken.application.service.JWTHelper
import kr.hhplus.be.server.queuetoken.domain.QueueToken
import kr.hhplus.be.server.queuetoken.exception.QueueTokenException
import org.slf4j.Logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
internal class EntryQueueService(
    private val entryQueuePort: EntryQueuePort,
    private val jwtHelper: JWTHelper
) {
    private val logger: Logger = Log.getLogger(EntryQueueService::class.java)

    @Transactional
    fun createEntryQueueToken(userId: UUID): String =
        Log.logging(logger) { log ->
            log["method"] = "createEntryQueueToken()"
            if (entryQueuePort.existsWaitingQueueToken(userId)) {
                throw QueueTokenException(ErrorCode.TOKEN_ALREADY_ISSUED, userId.toString())
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
