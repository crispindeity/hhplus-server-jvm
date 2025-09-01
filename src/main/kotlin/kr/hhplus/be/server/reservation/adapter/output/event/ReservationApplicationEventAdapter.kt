package kr.hhplus.be.server.reservation.adapter.output.event

import kr.hhplus.be.server.reservation.application.event.ReservationEvent
import kr.hhplus.be.server.reservation.application.port.ReservationEventPort
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
internal class ReservationApplicationEventAdapter(
    private val applicationEventPublisher: ApplicationEventPublisher
) : ReservationEventPort {
    override fun makeReservationEventPublish(event: ReservationEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}
