package kr.hhplus.be.server.concertseat.adapter.persistence.extensions

import kr.hhplus.be.server.common.adapter.persistence.entity.Version
import kr.hhplus.be.server.concert.application.service.dto.AvailableSeatDto
import kr.hhplus.be.server.concertseat.adapter.persistence.dto.AvailableSeatProjection
import kr.hhplus.be.server.concertseat.adapter.persistence.entity.ConcertSeatEntity
import kr.hhplus.be.server.concertseat.domain.ConcertSeat

internal fun ConcertSeatEntity.toDomain(): ConcertSeat =
    ConcertSeat(
        id = this.id!!,
        scheduleId = this.scheduleId,
        seatId = this.seatId,
        version = version.value,
        status =
            when (this.status) {
                ConcertSeatEntity.Status.HELD -> ConcertSeat.SeatStatus.HELD
                ConcertSeatEntity.Status.AVAILABLE -> ConcertSeat.SeatStatus.AVAILABLE
                ConcertSeatEntity.Status.RESERVED -> ConcertSeat.SeatStatus.RESERVED
            }
    )

internal fun AvailableSeatProjection.toDto(): AvailableSeatDto =
    AvailableSeatDto(
        id = this.concertSeatId,
        number = this.seatNumber,
        price = this.price,
        status =
            when (this.status) {
                ConcertSeatEntity.Status.HELD -> ConcertSeat.SeatStatus.HELD
                ConcertSeatEntity.Status.AVAILABLE -> ConcertSeat.SeatStatus.AVAILABLE
                ConcertSeatEntity.Status.RESERVED -> ConcertSeat.SeatStatus.RESERVED
            }
    )

internal fun ConcertSeat.toEntity(): ConcertSeatEntity =
    ConcertSeatEntity(
        id = this.id,
        scheduleId = this.scheduleId,
        seatId = this.seatId,
        version = Version(this.version),
        status =
            when (this.status) {
                ConcertSeat.SeatStatus.HELD -> ConcertSeatEntity.Status.HELD
                ConcertSeat.SeatStatus.AVAILABLE -> ConcertSeatEntity.Status.AVAILABLE
                ConcertSeat.SeatStatus.RESERVED -> ConcertSeatEntity.Status.RESERVED
            }
    )
