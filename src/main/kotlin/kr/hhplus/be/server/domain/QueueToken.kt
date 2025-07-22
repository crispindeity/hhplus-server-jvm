package kr.hhplus.be.server.domain

import java.util.UUID

data class QueueToken(
    var id: Long = 0L,
    val userId: UUID,
    val queueNumber: Int,
    val token: String,
    var status: Status = Status.WAITING
) {
    enum class Status {
        WAITING,
        COMPLETED,
        CANCELLED,
        EXPIRED
    }
}
