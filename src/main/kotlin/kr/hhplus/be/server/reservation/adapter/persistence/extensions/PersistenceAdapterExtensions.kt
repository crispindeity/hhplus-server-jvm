package kr.hhplus.be.server.reservation.adapter.persistence.extensions

import java.util.UUID
import kr.hhplus.be.server.common.adapter.persistence.entity.Version
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
            },
        version = Version(this.version)
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
            },
        version = Version(this.version)
    )

internal fun ReservationEntity.toDomain(): Reservation =
    Reservation(
        id = this.id!!,
        userId = UUID.fromString(this.userId),
        concertId = this.concertId,
        concertSeatId = this.concertSeatId,
        paymentId = this.paymentId,
        confirmedAt = this.confirmedAt,
        reservedAt = this.reservedAt,
        expiresAt = this.expiresAt,
        status =
            when (this.status) {
                ReservationEntity.Status.IN_PROGRESS -> Reservation.Status.IN_PROGRESS
                ReservationEntity.Status.CANCELLED -> Reservation.Status.CANCELLED
                ReservationEntity.Status.CONFIRMED -> Reservation.Status.CONFIRMED
                ReservationEntity.Status.EXPIRED -> Reservation.Status.EXPIRED
            },
        version = this.version.value
    )
