package kr.hhplus.be.server.adapter.web

import jakarta.validation.Valid
import java.util.UUID
import kr.hhplus.be.server.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.adapter.web.dto.request.ChargePointsRequest
import kr.hhplus.be.server.adapter.web.dto.response.FindUserPointResponse
import kr.hhplus.be.server.application.service.UserPointService
import kr.hhplus.be.server.common.annotation.RequireQueueAccess
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
internal class UserPointController(
    private val userPointService: UserPointService
) {
    @RequireQueueAccess
    @PostMapping("/{id}/points/charge")
    fun chargePoint(
        @PathVariable id: UUID,
        @RequestBody @Valid request: ChargePointsRequest
    ): ApiResponse<Long> {
        val response: Long = userPointService.chargePoint(userId = id, amount = request.amount)
        return ApiResponse.success(result = response)
    }

    @RequireQueueAccess
    @GetMapping("/{id}/points")
    fun findUserPoint(
        @PathVariable id: UUID
    ): ApiResponse<FindUserPointResponse> {
        val response = FindUserPointResponse(userId = id, balance = userPointService.getPoint(id))
        return ApiResponse.success(result = response)
    }
}
