package kr.hhplus.be.server.reservation.adapter.input.event

import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.reservation.application.event.ReservationEvent
import kr.hhplus.be.server.reservation.application.event.ReservationEventHandler
import org.slf4j.Logger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
internal class ReservationEventConsumer(
    private val reservationEventHandler: ReservationEventHandler
) {
    private val logger: Logger = Log.getLogger(this.javaClass)

    @KafkaListener(
        topics = ["concert.reservations"],
        groupId = "reservation-processing-group",
        concurrency = "3",
        containerFactory = "reservationEventKafkaListenerContainerFactory"
    )
    fun sendReservationInfo(
        @Payload event: ReservationEvent,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: String,
        @Header(KafkaHeaders.OFFSET) offset: Long
    ) {
        Log.logging(logger) { log ->
            log["method"] = "sendReservationInfo()"
            log["topic"] = "concert.reservations"
            log["partition"] = partition
            log["offset"] = offset
            log["eventId"] = event.eventId
            reservationEventHandler.handleReservationCreatedEvent(event)
        }
    }
}
