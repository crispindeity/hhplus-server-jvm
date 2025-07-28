package kr.hhplus.be.server.concertseat.adapter.persistence.dto

import kr.hhplus.be.server.concertseat.adapter.persistence.entity.ConcertSeatEntity

internal data class AvailableSeatProjection(
    val concertSeatId: Long,
    val seatNumber: Long,
    val price: Long,
    val status: ConcertSeatEntity.Status
)
