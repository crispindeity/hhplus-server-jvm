package kr.hhplus.be.server.reservation.application.service

import java.time.LocalDate
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.concertschedule.application.port.ConcertSchedulePort
import kr.hhplus.be.server.concertschedule.domain.ConcertSchedule
import kr.hhplus.be.server.concertschedule.exception.ConcertScheduleException
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.concertseat.domain.ConcertSeat
import kr.hhplus.be.server.concertseat.exception.ConcertSeatException
import kr.hhplus.be.server.reservation.application.service.extensions.orThrow
import kr.hhplus.be.server.seat.application.port.SeatPort
import kr.hhplus.be.server.seat.domain.Seat
import kr.hhplus.be.server.seat.exception.SeatException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

internal data class ReservationContext(
    val seat: Seat,
    val concertSeat: ConcertSeat,
    val schedule: ConcertSchedule
)

@Component
@Transactional(readOnly = true)
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
