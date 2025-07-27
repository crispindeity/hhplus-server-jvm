package kr.hhplus.be.server.queuetoken.adapter.persistence.extensions

import java.util.UUID
import kr.hhplus.be.server.queuetoken.adapter.persistence.entity.QueueTokenEntity
import kr.hhplus.be.server.queuetoken.domain.QueueToken

internal fun QueueToken.toEntity(): QueueTokenEntity =
    QueueTokenEntity(
        userId = this.userId.toString(),
        queueNumber = this.queueNumber,
        token = this.token,
        status = this.status.toEntity(),
        expiresAt = this.expiresAt
    )

internal fun QueueToken.Status.toEntity(): QueueTokenEntity.Status =
    when (this) {
        QueueToken.Status.WAITING -> QueueTokenEntity.Status.WAITING
        QueueToken.Status.COMPLETED -> QueueTokenEntity.Status.COMPLETED
        QueueToken.Status.CANCELLED -> QueueTokenEntity.Status.CANCELLED
        QueueToken.Status.EXPIRED -> QueueTokenEntity.Status.EXPIRED
    }

internal fun QueueTokenEntity.toDomain(): QueueToken =
    QueueToken(
        id = this.id!!,
        userId = UUID.fromString(this.userId),
        queueNumber = this.queueNumber,
        token = this.token,
        status = this.status.toDomain()
    )

internal fun QueueTokenEntity.Status.toDomain(): QueueToken.Status =
    when (this) {
        QueueTokenEntity.Status.WAITING -> QueueToken.Status.WAITING
        QueueTokenEntity.Status.COMPLETED -> QueueToken.Status.COMPLETED
        QueueTokenEntity.Status.CANCELLED -> QueueToken.Status.CANCELLED
        QueueTokenEntity.Status.EXPIRED -> QueueToken.Status.EXPIRED
    }

internal fun QueueToken.toUpdateEntity(): QueueTokenEntity =
    QueueTokenEntity(
        id = this.id,
        userId = this.userId.toString(),
        queueNumber = this.queueNumber,
        token = this.token,
        status = this.status.toEntity(),
        expiresAt = this.expiresAt
    )
