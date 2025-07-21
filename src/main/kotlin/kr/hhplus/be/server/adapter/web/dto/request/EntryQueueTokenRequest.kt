package kr.hhplus.be.server.adapter.web.dto.request

import kr.hhplus.be.server.common.annotation.NotEmptyOrBlank

internal data class EntryQueueTokenRequest(
    @NotEmptyOrBlank
    val userId: String
)
