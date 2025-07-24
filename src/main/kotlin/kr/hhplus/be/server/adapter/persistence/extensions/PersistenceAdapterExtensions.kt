package kr.hhplus.be.server.adapter.persistence.extensions

import java.time.Instant
import java.util.UUID
import kr.hhplus.be.server.adapter.persistence.dto.AvailableSeatProjection
import kr.hhplus.be.server.adapter.persistence.entity.ConcertScheduleEntity
import kr.hhplus.be.server.adapter.persistence.entity.ConcertSeatEntity
import kr.hhplus.be.server.adapter.persistence.entity.PointWalletEntity
import kr.hhplus.be.server.adapter.persistence.entity.QueueTokenJpaEntity
import kr.hhplus.be.server.adapter.persistence.entity.ReservationEntity
import kr.hhplus.be.server.adapter.persistence.entity.SeatHoldEntity
import kr.hhplus.be.server.application.service.dto.AvailableSeatDto
import kr.hhplus.be.server.domain.ConcertSchedule
import kr.hhplus.be.server.domain.ConcertSeat
import kr.hhplus.be.server.domain.PointWallet
import kr.hhplus.be.server.domain.QueueToken
import kr.hhplus.be.server.domain.Reservation
import kr.hhplus.be.server.domain.SeatHold

internal fun QueueToken.toEntity(): QueueTokenJpaEntity =
    QueueTokenJpaEntity(
        userId = this.userId.toString(),
        queueNumber = this.queueNumber,
        token = this.token,
        status = this.status.toEntity(),
        expiresAt = Instant.now().plusSeconds(60 * 60 * 24)
    )

internal fun QueueToken.Status.toEntity(): QueueTokenJpaEntity.Status =
    when (this) {
        QueueToken.Status.WAITING -> QueueTokenJpaEntity.Status.WAITING
        QueueToken.Status.COMPLETED -> QueueTokenJpaEntity.Status.COMPLETED
        QueueToken.Status.CANCELLED -> QueueTokenJpaEntity.Status.CANCELLED
        QueueToken.Status.EXPIRED -> QueueTokenJpaEntity.Status.EXPIRED
    }

internal fun QueueTokenJpaEntity.toDomain(): QueueToken =
    QueueToken(
        id = this.id!!,
        userId = UUID.fromString(this.userId),
        queueNumber = this.queueNumber,
        token = this.token,
        status = this.status.toDomain()
    )

internal fun QueueTokenJpaEntity.Status.toDomain(): QueueToken.Status =
    when (this) {
        QueueTokenJpaEntity.Status.WAITING -> QueueToken.Status.WAITING
        QueueTokenJpaEntity.Status.COMPLETED -> QueueToken.Status.COMPLETED
        QueueTokenJpaEntity.Status.CANCELLED -> QueueToken.Status.CANCELLED
        QueueTokenJpaEntity.Status.EXPIRED -> QueueToken.Status.EXPIRED
    }

internal fun ConcertScheduleEntity.toDomain(): ConcertSchedule =
    ConcertSchedule(
        id = this.id!!,
        concertId = this.concertId,
        date = this.date
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
                Reservation.Status.COMFIRMED -> ReservationEntity.Status.CONFIRMED
                Reservation.Status.EXPIRED -> ReservationEntity.Status.EXPIRED
            }
    )

internal fun SeatHold.toEntity(): SeatHoldEntity =
    SeatHoldEntity(
        concertSeatId = this.concertSeatId,
        userId = this.userId.toString(),
        heldAt = this.heldAt,
        expiresAt = this.expiresAt
    )

internal fun ConcertSeatEntity.toDomain(): ConcertSeat =
    ConcertSeat(
        id = this.id!!,
        scheduleId = this.scheduleId,
        seatId = this.seatId,
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
        status =
            when (this.status) {
                ConcertSeat.SeatStatus.HELD -> ConcertSeatEntity.Status.HELD
                ConcertSeat.SeatStatus.AVAILABLE -> ConcertSeatEntity.Status.AVAILABLE
                ConcertSeat.SeatStatus.RESERVED -> ConcertSeatEntity.Status.RESERVED
            }
    )

internal fun PointWalletEntity.toDomain(): PointWallet =
    PointWallet(
        id = this.id!!,
        userId = UUID.fromString(this.userId),
        balance = this.balance
    )

internal fun PointWallet.toEntity(): PointWalletEntity =
    PointWalletEntity(
        id = this.id,
        userId = this.userId.toString(),
        balance = this.balance
    )
