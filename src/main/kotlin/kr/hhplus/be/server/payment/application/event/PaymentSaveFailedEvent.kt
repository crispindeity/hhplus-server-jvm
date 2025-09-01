package kr.hhplus.be.server.payment.application.event

import java.util.UUID

internal data class PaymentSaveFailedEvent(
    val eventId: UUID,
    val reservationId: Long
)
