package kr.hhplus.be.server.adapter.persistence.extensions

import java.time.Instant
import java.util.UUID
import kr.hhplus.be.server.adapter.persistence.dto.AvailableSeatProjection
import kr.hhplus.be.server.adapter.persistence.entity.ConcertScheduleEntity
import kr.hhplus.be.server.adapter.persistence.entity.ConcertSeatEntity
import kr.hhplus.be.server.adapter.persistence.entity.QueueTokenJpaEntity
import kr.hhplus.be.server.application.service.dto.AvailableSeatDto
import kr.hhplus.be.server.domain.ConcertSchedule
import kr.hhplus.be.server.domain.ConcertSeat
import kr.hhplus.be.server.domain.QueueToken

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
