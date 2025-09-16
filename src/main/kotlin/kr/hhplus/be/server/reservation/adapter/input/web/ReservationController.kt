package kr.hhplus.be.server.reservation.adapter.input.web

import jakarta.servlet.ServletRequest
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kr.hhplus.be.server.common.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.common.annotation.RequireQueueAccess
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.reservation.adapter.input.web.request.MakeReservationRequest
import kr.hhplus.be.server.reservation.adapter.input.web.response.MakeReservationResponse
import kr.hhplus.be.server.reservation.application.service.ReservationService
import kr.hhplus.be.server.reservation.exception.ReservationException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reservations")
internal class ReservationController(
    private val reservationService: ReservationService
) {
    @PostMapping
    @RequireQueueAccess
    fun makeReservation(
        @RequestBody @Valid request: MakeReservationRequest,
        servletRequest: HttpServletRequest
    ): ApiResponse<MakeReservationResponse> {
        val userId: String =
            servletRequest.getUserIdOrNull()
                ?: throw ReservationException(ErrorCode.NOT_FOUND_USER_ID_IN_ATTRIBUTE)
        val response: MakeReservationResponse =
            reservationService.makeReservation(
                date = request.date,
                concertSeatId = request.seat,
                userId = userId
            )
        return ApiResponse.Companion.success(result = response)
    }

    private fun ServletRequest.getUserIdOrNull(): String? = this.getAttribute("userId") as String?
}
