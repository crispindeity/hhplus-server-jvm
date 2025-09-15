package kr.hhplus.be.server.reservation.adapter.output.event

import kr.hhplus.be.server.config.kafka.KafkaProperties
import kr.hhplus.be.server.reservation.application.event.ReservationEvent
import kr.hhplus.be.server.reservation.application.port.ReservationExternalEventPort
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
internal class ReservationKafkaEventAdapter(
    private val kafkaProperties: KafkaProperties,
    private val kafkaTemplate: KafkaTemplate<String, ReservationEvent>
) : ReservationExternalEventPort {
    override fun publishReservation(event: ReservationEvent) {
        kafkaTemplate.send(
            kafkaProperties.reservationTopic,
            event.reservationId.toString(),
            event
        )
    }
}
