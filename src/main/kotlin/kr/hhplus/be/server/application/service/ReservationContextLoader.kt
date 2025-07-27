package kr.hhplus.be.server.application.service

import java.time.LocalDate
import kr.hhplus.be.server.application.extensions.orThrow
import kr.hhplus.be.server.application.port.ConcertSchedulePort
import kr.hhplus.be.server.application.port.ConcertSeatPort
import kr.hhplus.be.server.application.port.SeatPort
import kr.hhplus.be.server.common.exception.ConcertScheduleException
import kr.hhplus.be.server.common.exception.ConcertSeatException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.exception.SeatException
import kr.hhplus.be.server.domain.ConcertSchedule
import kr.hhplus.be.server.domain.ConcertSeat
import kr.hhplus.be.server.domain.Seat
import org.springframework.stereotype.Component

internal data class ReservationContext(
    val seat: Seat,
    val concertSeat: ConcertSeat,
    val schedule: ConcertSchedule
)

@Component
internal class ReservationContextLoader(
    private val seatPort: SeatPort,
    private val concertSeatPort: ConcertSeatPort,
    private val concertSchedulePort: ConcertSchedulePort
) {
    fun load(
        concertSeatId: Long,
        date: LocalDate
    ): ReservationContext {
        val concertSeat: ConcertSeat =
            concertSeatPort
                .getConcertSeat(concertSeatId)
                .orThrow { ConcertSeatException(ErrorCode.NOT_FOUND_CONCERT_SEAT) }

        val schedule: ConcertSchedule =
            concertSchedulePort
                .getSchedule(concertSeat.scheduleId)
                .orThrow { ConcertScheduleException(ErrorCode.NOT_FOUND_CONCERT_SCHEDULE) }

        if (schedule.date != date) {
            throw ConcertScheduleException(ErrorCode.INVALID_CONCERT_DATE)
        }

        if (concertSeat.status != ConcertSeat.SeatStatus.AVAILABLE) {
            throw ConcertSeatException(ErrorCode.ALREADY_RESERVED)
        }

        val seat: Seat =
            seatPort
                .getSeat(concertSeat.seatId)
                .orThrow { SeatException(ErrorCode.NOT_FOUND_SEAT) }

        return ReservationContext(seat, concertSeat, schedule)
    }
}
