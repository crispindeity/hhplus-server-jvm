package kr.hhplus.be.server.application.service

import java.time.LocalDate
import kr.hhplus.be.server.adapter.web.dto.response.FindAvailableDatesResponse
import kr.hhplus.be.server.adapter.web.dto.response.FindAvailableSeatsResponses
import kr.hhplus.be.server.application.port.ConcertPort
import kr.hhplus.be.server.application.port.ConcertSchedulePort
import kr.hhplus.be.server.application.port.ConcertSeatPort
import kr.hhplus.be.server.application.service.dto.AvailableSeatDto
import kr.hhplus.be.server.application.service.extensions.toResponse
import kr.hhplus.be.server.common.exception.ConcertException
import kr.hhplus.be.server.common.exception.ConcertScheduleException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.domain.ConcertSchedule
import org.slf4j.Logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
internal class ConcertService(
    private val concertPort: ConcertPort,
    private val concertSchedulePort: ConcertSchedulePort,
    private val concertSeatPort: ConcertSeatPort
) {
    private val logger: Logger = Log.getLogger(ConcertService::class.java)

    fun getAvailableDates(concertId: Long): FindAvailableDatesResponse =
        Log.logging(logger) { log ->
            log["method"] = "getAvailableDates()"
            verifyConcert(concertId)
            val availableSchedules: List<ConcertSchedule> =
                concertSchedulePort.getAvailableSchedules(concertId)

            FindAvailableDatesResponse(
                availableSchedules.map { it.date }
            )
        }

    fun getAvailableSeats(
        concertId: Long,
        date: LocalDate
    ): FindAvailableSeatsResponses =
        Log.logging(logger) { log ->
            log["method"] = "getAvailableSeats()"
            verifyConcert(concertId)
            val schedule: ConcertSchedule =
                concertSchedulePort.getSchedule(concertId, date) ?: throw ConcertScheduleException(
                    ErrorCode.NOT_FOUND_CONCERT_SCHEDULE
                )
            val availableSeats: List<AvailableSeatDto> =
                concertSeatPort.getAvailableSeats(schedule.id)
            FindAvailableSeatsResponses(availableSeats.map { it.toResponse() })
        }

    private fun verifyConcert(concertId: Long) {
        if (!concertPort.existsConcert(concertId)) {
            throw ConcertException(ErrorCode.NOT_FOUND_CONCERT)
        }
    }
}
