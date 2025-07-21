package kr.hhplus.be.server.adapter.web

import jakarta.validation.Valid
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
        @RequestBody @Valid request: EntryQueueTokenRequest
    ): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity
            .ok()
            .header("EntryQueueToken", "token")
            .body(ApiResponse.success())
}
