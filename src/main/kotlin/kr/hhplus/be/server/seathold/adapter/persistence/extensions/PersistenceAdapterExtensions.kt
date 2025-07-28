package kr.hhplus.be.server.seathold.adapter.persistence.extensions

import kr.hhplus.be.server.seathold.adapter.persistence.entity.SeatHoldEntity
import kr.hhplus.be.server.seathold.domain.SeatHold

internal fun SeatHold.toEntity(): SeatHoldEntity =
    SeatHoldEntity(
        concertSeatId = this.concertSeatId,
        userId = this.userId.toString(),
        heldAt = this.heldAt,
        expiresAt = this.expiresAt
    )
