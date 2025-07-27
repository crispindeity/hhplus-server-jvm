package kr.hhplus.be.server.queuetoken.adapter.web.request

import kr.hhplus.be.server.common.annotation.NotEmptyOrBlank

internal data class EntryQueueTokenRequest(
    @NotEmptyOrBlank
    val userId: String
)
