package kr.hhplus.be.server.fake

import org.springframework.context.ApplicationEventPublisher

class FakeApplicationEventPublisher : ApplicationEventPublisher {
    override fun publishEvent(event: Any) {}
}
