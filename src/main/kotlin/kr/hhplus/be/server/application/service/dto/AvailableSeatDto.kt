package kr.hhplus.be.server.application.service.dto

import kr.hhplus.be.server.domain.ConcertSeat

internal data class AvailableSeatDto(
    val id: Long,
    val number: Long,
    val price: Long,
    val status: ConcertSeat.SeatStatus
)
