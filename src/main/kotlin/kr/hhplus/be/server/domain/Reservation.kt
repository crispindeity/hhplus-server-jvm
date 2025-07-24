package kr.hhplus.be.server.domain

import java.time.LocalDateTime
import java.util.UUID

internal data class Reservation(
    val id: Long = 0L,
    val userId: UUID,
    val concertId: Long,
    val concertSeatId: Long,
    val paymentId: Long? = null,
    val confirmedAt: LocalDateTime? = null,
    val reservedAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime = LocalDateTime.now().plusMinutes(6),
    val status: Status
) {
    enum class Status {
        IN_PROGRESS,
        CANCELLED,
        COMFIRMED,
        EXPIRED
    }
}
