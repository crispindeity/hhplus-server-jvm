package kr.hhplus.be.server.seat.adapter.persistence.extensions

import kr.hhplus.be.server.seat.adapter.persistence.entity.SeatEntity
import kr.hhplus.be.server.seat.domain.Seat

internal fun SeatEntity.toDomain(): Seat =
    Seat(
        id = this.id!!,
        number = this.number,
        price = this.price
    )
