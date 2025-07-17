package kr.hhplus.be.server.adapter.web

import kr.hhplus.be.server.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.adapter.web.dto.request.ChargePointsRequest
import org.springframework.http.ResponseEntity
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
        @RequestBody request: ChargePointsRequest
    ): ResponseEntity<ApiResponse<Long>> {
        if (request.amount > 0) {
            return ResponseEntity.ok().body(ApiResponse.success(result = id))
        }
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.fail(code = 400, message = "bad request"))
    }
}
