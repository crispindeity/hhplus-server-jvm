package kr.hhplus.be.server.reservation.adapter.output.event

import kr.hhplus.be.server.reservation.application.event.ReservationEvent
import kr.hhplus.be.server.reservation.application.port.ReservationInternalEventPort
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
internal class ReservationApplicationEventAdapter(
    private val applicationEventPublisher: ApplicationEventPublisher
) : ReservationInternalEventPort {
    override fun publishReservation(event: ReservationEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}
