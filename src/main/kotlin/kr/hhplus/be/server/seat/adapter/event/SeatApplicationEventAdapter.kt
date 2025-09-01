package kr.hhplus.be.server.seat.adapter.event

import kr.hhplus.be.server.seat.application.event.SeatHoldCompletedEvent
import kr.hhplus.be.server.seat.application.event.SeatHoldFailedEvent
import kr.hhplus.be.server.seat.application.port.SeatEventPort
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
internal class SeatApplicationEventAdapter(
    private val applicationEventPublisher: ApplicationEventPublisher
) : SeatEventPort {
    override fun holdSeatCompletedEventPublish(event: SeatHoldCompletedEvent) {
        applicationEventPublisher.publishEvent(event)
    }

    override fun holdSeatFailEventPublish(event: SeatHoldFailedEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}
