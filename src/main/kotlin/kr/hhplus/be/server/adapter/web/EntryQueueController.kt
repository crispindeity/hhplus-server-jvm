package kr.hhplus.be.server.adapter.web

import kr.hhplus.be.server.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.adapter.web.dto.request.EntryQueueTokenRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/queue")
internal class EntryQueueController {
    @PostMapping("/entry-token")
    fun getEntryQueueToken(
        @RequestBody request: EntryQueueTokenRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        if (request.userId.isNotBlank()) {
            return ResponseEntity
                .ok()
                .header("EntryQueueToken", "token")
                .body(ApiResponse.success())
        }
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.fail(400, "bad request"))
    }
}
