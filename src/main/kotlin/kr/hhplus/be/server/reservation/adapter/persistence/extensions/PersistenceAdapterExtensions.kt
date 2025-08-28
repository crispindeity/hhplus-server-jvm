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
        status = this.status.toEntity(),
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
        status = this.status.toEntity(),
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
        status = this.status.toDomain(),
        version = this.version.value
    )

internal fun Reservation.Status.toEntity(): ReservationEntity.Status =
    when (this) {
        Reservation.Status.INIT -> ReservationEntity.Status.INIT
        Reservation.Status.IN_PROGRESS -> ReservationEntity.Status.IN_PROGRESS
        Reservation.Status.CANCELLED -> ReservationEntity.Status.CANCELLED
        Reservation.Status.CONFIRMED -> ReservationEntity.Status.CONFIRMED
        Reservation.Status.EXPIRED -> ReservationEntity.Status.EXPIRED
        Reservation.Status.ERROR -> ReservationEntity.Status.ERROR
    }

internal fun ReservationEntity.Status.toDomain(): Reservation.Status =
    when (this) {
        ReservationEntity.Status.INIT -> Reservation.Status.INIT
        ReservationEntity.Status.IN_PROGRESS -> Reservation.Status.IN_PROGRESS
        ReservationEntity.Status.CANCELLED -> Reservation.Status.CANCELLED
        ReservationEntity.Status.CONFIRMED -> Reservation.Status.CONFIRMED
        ReservationEntity.Status.EXPIRED -> Reservation.Status.EXPIRED
        ReservationEntity.Status.ERROR -> Reservation.Status.ERROR
    }
