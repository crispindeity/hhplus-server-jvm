package kr.hhplus.be.server.adapter.web

import jakarta.validation.Valid
import java.time.LocalDateTime
import kr.hhplus.be.server.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.adapter.web.dto.request.MakeReservationRequest
import kr.hhplus.be.server.adapter.web.dto.response.MakeReservationResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reservations")
internal class ReservationController {
    @PostMapping
    fun makeReservation(
        @RequestBody @Valid request: MakeReservationRequest
    ): ApiResponse<MakeReservationResponse> {
        val response =
            MakeReservationResponse(
                id = 1L,
                userId = 1L,
                concertId = 1L,
                reservedAt = LocalDateTime.now(),
                expiresAt = LocalDateTime.now().plusMinutes(5)
            )
        return ApiResponse.success(result = response)
    }
}
