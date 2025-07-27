package kr.hhplus.be.server.domain

import java.time.Instant
import java.util.UUID
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode

data class QueueToken(
    var id: Long = 0L,
    val userId: UUID,
    val queueNumber: Int,
    val token: String,
    var status: Status = Status.WAITING,
    val expiresAt: Instant = Instant.now().plusSeconds(60 * 60 * 24)
) {
    enum class Status {
        WAITING,
        COMPLETED,
        CANCELLED,
        EXPIRED
    }

    fun completed(): QueueToken {
        if (Status.WAITING != status) {
            throw CustomException(
                codeInterface = ErrorCode.INVALID_STATUS,
                additionalMessage = "queueStatus: $status"
            )
        }
        return this.copy(
            status = Status.COMPLETED
        )
    }
}
