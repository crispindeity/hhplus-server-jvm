package kr.hhplus.be.server.domain

import java.time.LocalDateTime
import java.util.UUID

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
}
