package kr.hhplus.be.server.adapter.web.dto.response

internal data class FindAvailableSeatsResponses(
    val seats: List<FindAvailableSeatsResponse>
)

internal data class FindAvailableSeatsResponse(
    val id: Long,
    val number: Long,
    val price: Long
)
