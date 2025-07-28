package kr.hhplus.be.server.queuetoken.domain

import java.time.Instant
import java.util.UUID
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.queuetoken.exception.QueueTokenException

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
            throw QueueTokenException(
                code = ErrorCode.INVALID_STATUS,
                message = "queueStatus: $status"
            )
        }
        return this.copy(
            status = Status.COMPLETED
        )
    }
}
