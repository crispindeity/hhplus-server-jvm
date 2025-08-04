package kr.hhplus.be.server.config

import kr.hhplus.be.server.seat.application.port.SeatPort
import kr.hhplus.be.server.seathold.application.port.SeatHoldPort
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
internal class SeatTestConfig {
    @Bean
    fun seatHoldPort(): SeatHoldPort = mock(SeatHoldPort::class.java)

    @Bean
    fun seatPort(): SeatPort = mock(SeatPort::class.java)
}
