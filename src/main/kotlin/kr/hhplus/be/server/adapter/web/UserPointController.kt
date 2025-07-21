package kr.hhplus.be.server.adapter.web

import jakarta.validation.Valid
import kr.hhplus.be.server.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.adapter.web.dto.request.ChargePointsRequest
import kr.hhplus.be.server.adapter.web.dto.response.FindUserPointResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
internal class UserPointController {
    @PostMapping("/{id}/points/charge")
    fun chargePoint(
        @PathVariable id: Long,
        @RequestBody @Valid request: ChargePointsRequest
    ): ApiResponse<Long> = ApiResponse.success(result = id)

    @GetMapping("/{id}/points")
    fun findUserPoint(
        @PathVariable id: Long
    ): ApiResponse<FindUserPointResponse> {
        val response =
            FindUserPointResponse(
                userId = id,
                balance = 1000L
            )
        return ApiResponse.success(result = response)
    }
}
