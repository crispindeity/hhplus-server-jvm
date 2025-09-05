package kr.hhplus.be.server.reservation.adapter.output.event

import kr.hhplus.be.server.reservation.application.event.ReservationEvent
import kr.hhplus.be.server.reservation.application.port.ReservationExternalEventPort
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
internal class ReservationKafkaEventAdapter(
    private val kafkaProperties: KafkaProperties,
    private val kafkaTemplate: KafkaTemplate<String, ReservationEvent>
) : ReservationExternalEventPort {
    override fun publishReservation(event: ReservationEvent) {
        TODO("Not yet implemented")
    }
}
