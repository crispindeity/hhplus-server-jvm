package kr.hhplus.be.server.reservation.domain

import java.time.LocalDateTime
import java.util.UUID

internal data class ReservationEventTrace(
    val id: Long = 0L,
    val eventId: UUID,
    val reservationId: Long,
    val eventType: EventType,
    val occurredAt: LocalDateTime = LocalDateTime.now()
) {
    enum class EventType {
        PAYMENT,
        SEAT_HELD,
        CONCERT_SEAT_HELD
    }
}
