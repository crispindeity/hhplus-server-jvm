package kr.hhplus.be.server.queuetoken.adapter.web

import jakarta.validation.Valid
import java.util.UUID
import kr.hhplus.be.server.common.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.queuetoken.adapter.web.request.EntryQueueTokenRequest
import kr.hhplus.be.server.queuetoken.application.service.EntryQueueService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/queue")
internal class EntryQueueController(
    private val service: EntryQueueService
) {
    @PostMapping("/entry-token")
    fun getEntryQueueToken(
        @RequestBody @Valid request: EntryQueueTokenRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val token: String = service.createEntryQueueToken(UUID.fromString(request.userId))
        return ResponseEntity
            .ok()
            .header(
                "EntryQueueToken",
                token
            ).body(ApiResponse.Companion.success())
    }
}
