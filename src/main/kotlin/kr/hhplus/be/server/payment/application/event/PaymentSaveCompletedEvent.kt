package kr.hhplus.be.server.payment.application.event

import java.util.UUID

internal data class PaymentSaveCompletedEvent(
    val eventId: UUID,
    val reservationId: Long,
    val paymentId: Long
)
