package kr.hhplus.be.server.seat.application.port

import kr.hhplus.be.server.seat.application.event.SeatHoldCompletedEvent
import kr.hhplus.be.server.seat.application.event.SeatHoldFailedEvent

internal interface SeatEventPort {
    fun holdSeatCompletedEventPublish(event: SeatHoldCompletedEvent)

    fun holdSeatFailEventPublish(event: SeatHoldFailedEvent)
}
