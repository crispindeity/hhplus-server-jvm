package kr.hhplus.be.server.payment.domain

import java.time.LocalDateTime
import java.util.UUID
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.payment.exception.PaymentException

internal data class Payment(
    val id: Long = 0L,
    val userId: UUID,
    val price: Long,
    val paidAt: LocalDateTime? = null,
    val status: Status = Status.PENDING,
    val version: Int = 0
) {
    enum class Status {
        PENDING,
        COMPLETED,
        CANCELLED
    }

    fun complete(now: LocalDateTime = LocalDateTime.now()): Payment {
        if (status != Status.PENDING) {
            throw PaymentException(
                code = ErrorCode.INVALID_STATUS,
                message = "paymentStatus: $status"
            )
        }
        return this.copy(
            status = Status.COMPLETED,
            paidAt = now
        )
    }
}
