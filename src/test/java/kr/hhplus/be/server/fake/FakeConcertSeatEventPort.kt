package kr.hhplus.be.server.fake

import kr.hhplus.be.server.concertseat.application.event.ConcertSeatHoldCompletedEvent
import kr.hhplus.be.server.concertseat.application.event.ConcertSeatHoldFailedEvent
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatEventPort

internal class FakeConcertSeatEventPort : ConcertSeatEventPort {
    override fun holdConcertSeatCompletedEventPublish(event: ConcertSeatHoldCompletedEvent) {}

    override fun holdConcertSeatFailEventPublish(event: ConcertSeatHoldFailedEvent) {}
}
