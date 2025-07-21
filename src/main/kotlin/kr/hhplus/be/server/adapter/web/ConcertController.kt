package kr.hhplus.be.server.adapter.web

import java.time.LocalDate
import kr.hhplus.be.server.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.adapter.web.dto.response.FindAvailableDatesResponse
import kr.hhplus.be.server.adapter.web.dto.response.FindAvailableSeatsResponse
import kr.hhplus.be.server.adapter.web.dto.response.FindAvailableSeatsResponses
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/concerts")
internal class ConcertController {
    @GetMapping("/{id}/reservations/available-dates")
    fun findAvailableDates(
        @PathVariable id: Long
    ): ApiResponse<FindAvailableDatesResponse> {
        val response =
            FindAvailableDatesResponse(
                listOf(
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(2),
                    LocalDate.now().plusDays(3)
                )
            )
        return ApiResponse.success(result = response)
    }

    @GetMapping("/{id}/reservations/available-seats")
    fun findAvailableSeats(
        @PathVariable id: Long,
        @RequestParam date: LocalDate
    ): ApiResponse<FindAvailableSeatsResponses> {
        val response =
            FindAvailableSeatsResponses(
                listOf(
                    FindAvailableSeatsResponse(
                        id = 1L,
                        number = 1,
                        price = 1000
                    ),
                    FindAvailableSeatsResponse(
                        id = 2L,
                        number = 2,
                        price = 2000
                    ),
                    FindAvailableSeatsResponse(
                        id = 3L,
                        number = 3,
                        price = 3000
                    )
                )
            )
        return ApiResponse.success(result = response)
    }
}
