package kr.hhplus.be.server.reservation.domain

import java.time.LocalDateTime
import java.util.UUID
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.reservation.exception.ReservationException

internal data class Reservation(
    val id: Long = 0L,
    val userId: UUID,
    val concertId: Long,
    val concertSeatId: Long,
    val paymentId: Long? = null,
    val confirmedAt: LocalDateTime? = null,
    val reservedAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime = LocalDateTime.now().plusMinutes(6),
    val status: Status,
    val version: Int = 0
) {
    enum class Status {
        INIT,
        IN_PROGRESS,
        CANCELLED,
        CONFIRMED,
        EXPIRED,
        ERROR
    }

    fun confirm(now: LocalDateTime = LocalDateTime.now()): Reservation {
        if (status != Status.IN_PROGRESS) {
            throw ReservationException(
                code = ErrorCode.INVALID_STATUS,
                message = "ReservationStatus: $status"
            )
        }
        return this.copy(
            status = Status.CONFIRMED,
            reservedAt = now
        )
    }

    fun error(): Reservation {
        if (status != Status.INIT) {
            throw ReservationException(
                code = ErrorCode.INVALID_STATUS,
                message = "ReservationStatus: $status"
            )
        }
        return this.copy(
            status = Status.ERROR
        )
    }

    fun inProgress(): Reservation {
        if (status != Status.INIT) {
            throw ReservationException(
                code = ErrorCode.INVALID_STATUS,
                message = "ReservationStatus: $status"
            )
        }
        return this.copy(
            status = Status.IN_PROGRESS
        )
    }

    fun isStatusAsError(): Boolean = status == Status.ERROR

    fun isStatusAsInProgress(): Boolean = status == Status.IN_PROGRESS

    fun updatePayment(paymentId: Long): Reservation {
        if (this.paymentId != null || status != Status.INIT) {
            throw ReservationException(
                code = ErrorCode.INVALID_STATUS,
                message = "ReservationStatus: $status, PaymentId: $paymentId"
            )
        }
        return this.copy(
            paymentId = paymentId
        )
    }
}
