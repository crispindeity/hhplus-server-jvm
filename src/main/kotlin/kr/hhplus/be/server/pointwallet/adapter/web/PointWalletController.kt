package kr.hhplus.be.server.pointwallet.adapter.web

import jakarta.validation.Valid
import java.util.UUID
import kr.hhplus.be.server.common.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.common.annotation.RequireQueueAccess
import kr.hhplus.be.server.pointwallet.adapter.web.request.ChargePointsRequest
import kr.hhplus.be.server.pointwallet.adapter.web.response.FindUserPointResponse
import kr.hhplus.be.server.pointwallet.application.service.PointWalletService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
internal class PointWalletController(
    private val userPointService: PointWalletService
) {
    @RequireQueueAccess
    @PostMapping("/{id}/points/charge")
    fun chargePoint(
        @PathVariable id: UUID,
        @RequestBody @Valid request: ChargePointsRequest
    ): ApiResponse<Long> {
        val response: Long = userPointService.chargePoint(userId = id, amount = request.amount)
        return ApiResponse.Companion.success(result = response)
    }

    @RequireQueueAccess
    @GetMapping("/{id}/points")
    fun findUserPoint(
        @PathVariable id: UUID
    ): ApiResponse<FindUserPointResponse> {
        val response = FindUserPointResponse(userId = id, balance = userPointService.getPoint(id))
        return ApiResponse.Companion.success(result = response)
    }
}
