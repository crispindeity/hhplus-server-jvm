package kr.hhplus.be.server.adapter.web

import java.time.LocalDate
import java.time.LocalDateTime
import kr.hhplus.be.server.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.adapter.web.dto.request.FindAvailableDatesRequest
import kr.hhplus.be.server.adapter.web.dto.request.MakeReservationRequest
import kr.hhplus.be.server.adapter.web.dto.response.FindAvailableDatesResponse
import kr.hhplus.be.server.adapter.web.dto.response.FindAvailableSeatsResponse
import kr.hhplus.be.server.adapter.web.dto.response.FindAvailableSeatsResponses
import kr.hhplus.be.server.adapter.web.dto.response.MakeReservationResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reservations")
internal class ReservationController {
    @GetMapping("/available-dates")
    fun findAvailableDates(
        @RequestBody request: FindAvailableDatesRequest
    ): ResponseEntity<ApiResponse<FindAvailableDatesResponse>?> {
        val response =
            FindAvailableDatesResponse(
                listOf(
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(2),
                    LocalDate.now().plusDays(3)
                )
            )
        if (request.concertId > 0) {
            return ResponseEntity.ok().body(ApiResponse.success(result = response))
        }
        return ResponseEntity.badRequest().body(ApiResponse.fail(400, "bad request"))
    }

    @GetMapping("/available-seats")
    fun findAvailableSeats(
        @RequestParam date: LocalDate
    ): ResponseEntity<ApiResponse<FindAvailableSeatsResponses>> {
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
        if (date.isAfter(LocalDate.now().minusDays(1))) {
            return ResponseEntity.ok().body(ApiResponse.success(result = response))
        }
        return ResponseEntity.badRequest().body(ApiResponse.fail(400, "bad request"))
    }

    @PostMapping
    fun makeReservation(
        @RequestBody request: MakeReservationRequest
    ): ResponseEntity<ApiResponse<MakeReservationResponse>> {
        val response =
            MakeReservationResponse(
                id = 1L,
                userId = 1L,
                concertId = 1L,
                reservedAt = LocalDateTime.now(),
                expiresAt = LocalDateTime.now().plusMinutes(5)
            )
        if (request.seat >= 1 && request.date.isAfter(LocalDate.now().minusDays(1))) {
            return ResponseEntity.ok().body(ApiResponse.success(result = response))
        }
        return ResponseEntity.badRequest().body(ApiResponse.fail(400, "bad request"))
    }
}
