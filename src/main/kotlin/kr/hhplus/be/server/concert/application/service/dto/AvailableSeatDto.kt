package kr.hhplus.be.server.concert.application.service.dto

import kr.hhplus.be.server.concertseat.domain.ConcertSeat

internal data class AvailableSeatDto(
    val id: Long,
    val number: Long,
    val price: Long,
    val status: ConcertSeat.SeatStatus
)
