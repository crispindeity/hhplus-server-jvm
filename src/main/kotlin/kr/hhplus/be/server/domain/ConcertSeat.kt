package kr.hhplus.be.server.domain

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
}
