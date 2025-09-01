package kr.hhplus.be.server.fake

import org.springframework.context.ApplicationEventPublisher

class FakeReservationEventPublisher : ApplicationEventPublisher {
    override fun publishEvent(event: Any) {}
}
