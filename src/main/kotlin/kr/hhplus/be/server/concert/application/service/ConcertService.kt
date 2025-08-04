package kr.hhplus.be.server.concert.application.service

import java.time.LocalDate
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concert.adapter.web.response.FindAvailableDatesResponse
import kr.hhplus.be.server.concert.adapter.web.response.FindAvailableSeatsResponses
import kr.hhplus.be.server.concert.application.port.ConcertPort
import kr.hhplus.be.server.concert.application.service.dto.AvailableSeatDto
import kr.hhplus.be.server.concert.application.service.extensions.toResponse
import kr.hhplus.be.server.concert.exception.ConcertException
import kr.hhplus.be.server.concertschedule.application.port.ConcertSchedulePort
import kr.hhplus.be.server.concertschedule.domain.ConcertSchedule
import kr.hhplus.be.server.concertschedule.exception.ConcertScheduleException
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
internal class ConcertService(
    private val concertPort: ConcertPort,
    private val concertSchedulePort: ConcertSchedulePort,
    private val concertSeatPort: ConcertSeatPort,
    private val transactional: Transactional
) {
    private val logger: Logger = Log.getLogger(ConcertService::class.java)

    fun getAvailableDates(concertId: Long): FindAvailableDatesResponse =
        Log.logging(logger) { log ->
            log["method"] = "getAvailableDates()"
            verifyConcert(concertId)

            val availableSchedules: List<ConcertSchedule> =
                transactional.readOnly {
                    concertSchedulePort.getAvailableSchedules(concertId)
                }

            FindAvailableDatesResponse(availableSchedules.map { it.date })
        }

    fun getAvailableSeats(
        concertId: Long,
        date: LocalDate
    ): FindAvailableSeatsResponses =
        Log.logging(logger) { log ->
            log["method"] = "getAvailableSeats()"
            verifyConcert(concertId)

            val availableSeats: List<AvailableSeatDto> =
                transactional.readOnly {
                    val schedule: ConcertSchedule =
                        concertSchedulePort.getSchedule(concertId, date)
                            ?: throw ConcertScheduleException(ErrorCode.NOT_FOUND_CONCERT_SCHEDULE)
                    concertSeatPort.getAvailableSeats(schedule.id)
                }

            FindAvailableSeatsResponses(availableSeats.map { it.toResponse() })
        }

    private fun verifyConcert(concertId: Long) {
        if (!concertPort.existsConcert(concertId)) {
            throw ConcertException(ErrorCode.NOT_FOUND_CONCERT)
        }
    }
}
