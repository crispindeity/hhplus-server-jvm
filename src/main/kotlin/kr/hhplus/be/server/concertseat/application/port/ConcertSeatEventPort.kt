package kr.hhplus.be.server.concertseat.application.port

import kr.hhplus.be.server.concertseat.application.event.ConcertSeatHoldCompletedEvent
import kr.hhplus.be.server.concertseat.application.event.ConcertSeatHoldFailedEvent

internal interface ConcertSeatEventPort {
    fun holdConcertSeatCompletedEventPublish(event: ConcertSeatHoldCompletedEvent)

    fun holdConcertSeatFailEventPublish(event: ConcertSeatHoldFailedEvent)
}
