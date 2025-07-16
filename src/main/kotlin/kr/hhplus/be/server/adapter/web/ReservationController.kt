package kr.hhplus.be.server.adapter.web

import java.time.LocalDate
import kr.hhplus.be.server.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.adapter.web.dto.request.FindAvailableDatesRequest
import kr.hhplus.be.server.adapter.web.dto.response.FindAvailableDatesResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
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
}
