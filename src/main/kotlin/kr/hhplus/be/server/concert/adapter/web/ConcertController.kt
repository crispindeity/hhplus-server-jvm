package kr.hhplus.be.server.concert.adapter.web

import java.time.LocalDate
import kr.hhplus.be.server.common.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.common.annotation.RequireQueueAccess
import kr.hhplus.be.server.concert.adapter.web.response.FindAvailableDatesResponse
import kr.hhplus.be.server.concert.adapter.web.response.FindAvailableSeatsResponses
import kr.hhplus.be.server.concert.application.service.ConcertService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/concerts")
internal class ConcertController(
    private val concertService: ConcertService
) {
    @RequireQueueAccess
    @GetMapping("/{id}/reservations/available-dates")
    fun findAvailableDates(
        @PathVariable id: Long
    ): ApiResponse<FindAvailableDatesResponse> {
        val response: FindAvailableDatesResponse = concertService.getAvailableDates(id)
        return ApiResponse.Companion.success(result = response)
    }

    @RequireQueueAccess
    @GetMapping("/{id}/reservations/available-seats")
    fun findAvailableSeats(
        @PathVariable id: Long,
        @RequestParam date: LocalDate
    ): ApiResponse<FindAvailableSeatsResponses> {
        val response: FindAvailableSeatsResponses =
            concertService.getAvailableSeats(concertId = id, date = date)
        return ApiResponse.Companion.success(result = response)
    }
}
