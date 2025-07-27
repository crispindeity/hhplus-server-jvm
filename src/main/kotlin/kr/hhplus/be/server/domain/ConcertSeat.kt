package kr.hhplus.be.server.domain

import kr.hhplus.be.server.common.exception.ConcertSeatException
import kr.hhplus.be.server.common.exception.ErrorCode

internal data class ConcertSeat(
    val id: Long,
    val scheduleId: Long,
    val seatId: Long,
    val status: SeatStatus
) {
    enum class SeatStatus {
        HELD,
        AVAILABLE,
        RESERVED
    }

    fun held(): ConcertSeat {
        if (status != SeatStatus.AVAILABLE) {
            throw ConcertSeatException(ErrorCode.ALREADY_RESERVED)
        }
        return this.copy(
            status = SeatStatus.HELD
        )
    }

    fun reserved(): ConcertSeat {
        if (status != SeatStatus.HELD) {
            throw ConcertSeatException(
                code = ErrorCode.INVALID_STATUS,
                message = "SeatStatus: $status"
            )
        }
        return this.copy(
            status = SeatStatus.RESERVED
        )
    }
}
