package kr.hhplus.be.server.domain

import java.time.LocalDateTime
import java.util.UUID
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode

internal data class Payment(
    val id: Long = 0L,
    val userId: UUID,
    val price: Long,
    val paidAt: LocalDateTime? = null,
    val status: Status = Status.PENDING
) {
    enum class Status {
        PENDING,
        COMPLETED,
        CANCELLED
    }

    fun complete(): Payment {
        if (status != Status.PENDING) {
            throw CustomException(
                codeInterface = ErrorCode.INVALID_STATUS,
                additionalMessage = "paymentStatus: $status"
            )
        }
        return this.copy(
            status = Status.COMPLETED
        )
    }
}
