package kr.hhplus.be.server.config

import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concert.application.port.ConcertPort
import kr.hhplus.be.server.concert.application.service.ConcertService
import kr.hhplus.be.server.concertschedule.application.port.ConcertSchedulePort
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
internal class ConcertTestConfig {
    @Bean
    fun concertPort(): ConcertPort = mock(ConcertPort::class.java)

    @Bean
    fun concertSchedulePort(): ConcertSchedulePort = mock(ConcertSchedulePort::class.java)

    @Bean
    fun concertSeatPort(): ConcertSeatPort = mock(ConcertSeatPort::class.java)

    @Bean
    fun concertService(
        concertPort: ConcertPort,
        concertSchedulePort: ConcertSchedulePort,
        concertSeatPort: ConcertSeatPort,
        transactional: Transactional
    ): ConcertService =
        ConcertService(concertPort, concertSchedulePort, concertSeatPort, transactional)
}
