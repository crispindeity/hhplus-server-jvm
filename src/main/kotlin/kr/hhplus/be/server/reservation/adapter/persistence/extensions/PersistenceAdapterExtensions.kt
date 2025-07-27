package kr.hhplus.be.server.reservation.adapter.persistence.extensions

import kr.hhplus.be.server.reservation.adapter.persistence.entity.ReservationEntity
import kr.hhplus.be.server.reservation.domain.Reservation

internal fun Reservation.toEntity(): ReservationEntity =
    ReservationEntity(
        userId = this.userId.toString(),
        concertId = this.concertId,
        paymentId = this.paymentId,
        concertSeatId = this.concertSeatId,
        confirmedAt = this.confirmedAt,
        reservedAt = this.reservedAt,
        expiresAt = this.expiresAt,
        status =
            when (this.status) {
                Reservation.Status.IN_PROGRESS -> ReservationEntity.Status.IN_PROGRESS
                Reservation.Status.CANCELLED -> ReservationEntity.Status.CANCELLED
                Reservation.Status.CONFIRMED -> ReservationEntity.Status.CONFIRMED
                Reservation.Status.EXPIRED -> ReservationEntity.Status.EXPIRED
            }
    )

internal fun Reservation.toUpdateEntity(): ReservationEntity =
    ReservationEntity(
        id = this.id,
        userId = this.userId.toString(),
        concertId = this.concertId,
        paymentId = this.paymentId,
        concertSeatId = this.concertSeatId,
        confirmedAt = this.confirmedAt,
        reservedAt = this.reservedAt,
        expiresAt = this.expiresAt,
        status =
            when (this.status) {
                Reservation.Status.IN_PROGRESS -> ReservationEntity.Status.IN_PROGRESS
                Reservation.Status.CANCELLED -> ReservationEntity.Status.CANCELLED
                Reservation.Status.CONFIRMED -> ReservationEntity.Status.CONFIRMED
                Reservation.Status.EXPIRED -> ReservationEntity.Status.EXPIRED
            }
    )
