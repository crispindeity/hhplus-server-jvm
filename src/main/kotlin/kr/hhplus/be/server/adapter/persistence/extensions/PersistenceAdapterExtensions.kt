package kr.hhplus.be.server.adapter.persistence.extensions

import java.time.Instant
import java.util.UUID
import kr.hhplus.be.server.adapter.persistence.entity.QueueTokenJpaEntity
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
