package kr.hhplus.be.server.adapter.web

import java.time.LocalDateTime
import kr.hhplus.be.server.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.adapter.web.dto.request.PayWithPointsRequest
import kr.hhplus.be.server.adapter.web.dto.response.PayWithPointsResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reservations")
internal class PaymentController {
    @PostMapping("/{id}/payment/points")
    fun payWithPoints(
        @PathVariable id: Long,
        @RequestBody request: PayWithPointsRequest
    ): ResponseEntity<ApiResponse<PayWithPointsResponse>?> {
        val response =
            PayWithPointsResponse(
                userId = 1L,
                reservationId = 1L,
                price = 1000L,
                paidAt = LocalDateTime.now()
            )
        if (request.price >= 0) {
            return ResponseEntity.ok().body(ApiResponse.success(result = response))
        }
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.fail(code = 400, message = "bad request"))
    }
}
