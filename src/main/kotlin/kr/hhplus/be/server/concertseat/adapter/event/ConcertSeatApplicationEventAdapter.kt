package kr.hhplus.be.server.concertseat.adapter.event

import kr.hhplus.be.server.concertseat.application.event.ConcertSeatHoldCompletedEvent
import kr.hhplus.be.server.concertseat.application.event.ConcertSeatHoldFailedEvent
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatEventPort
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
internal class ConcertSeatApplicationEventAdapter(
    private val applicationEventPublisher: ApplicationEventPublisher
) : ConcertSeatEventPort {
    override fun holdConcertSeatCompletedEventPublish(event: ConcertSeatHoldCompletedEvent) {
        applicationEventPublisher.publishEvent(event)
    }

    override fun holdConcertSeatFailEventPublish(event: ConcertSeatHoldFailedEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}
