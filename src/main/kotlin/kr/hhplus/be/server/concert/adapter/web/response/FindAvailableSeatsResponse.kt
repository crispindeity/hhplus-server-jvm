package kr.hhplus.be.server.concert.adapter.web.response

internal data class FindAvailableSeatsResponses(
    val seats: List<FindAvailableSeatsResponse>
)

internal data class FindAvailableSeatsResponse(
    val id: Long,
    val number: Long,
    val price: Long
)
