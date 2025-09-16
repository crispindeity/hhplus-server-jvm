package kr.hhplus.be.server.config

import kr.hhplus.be.server.common.transactional.AfterCommitExecutor
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concertschedule.application.port.ConcertSchedulePort
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.reservation.application.port.ReservationExternalEventPort
import kr.hhplus.be.server.reservation.application.port.ReservationInternalEventPort
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.reservation.application.service.ReservationContextLoader
import kr.hhplus.be.server.reservation.application.service.ReservationService
import kr.hhplus.be.server.seat.application.port.SeatPort
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean

@TestConfiguration
internal class ReservationTestConfig {
    @Bean
    fun reservationService(
        reservationPort: ReservationPort,
        reservationContextLoader: ReservationContextLoader,
        transactional: Transactional,
        afterCommitExecutor: AfterCommitExecutor,
        externalEventPort: ReservationExternalEventPort,
        internalEventPort: ReservationInternalEventPort
    ): ReservationService =
        ReservationService(
            reservationPort = reservationPort,
            reservationContextLoader = reservationContextLoader,
            transactional = transactional,
            afterCommitExecutor = afterCommitExecutor,
            externalEventPort = externalEventPort,
            internalEventPort = internalEventPort
        )

    @Bean
    fun reservationContextLoader(
        seatPort: SeatPort,
        concertSeatPort: ConcertSeatPort,
        concertSchedulePort: ConcertSchedulePort
    ): ReservationContextLoader =
        ReservationContextLoader(
            seatPort,
            concertSeatPort,
            concertSchedulePort
        )

    @Bean
    fun reservationPort(): ReservationPort = mock(ReservationPort::class.java)

    @Bean
    fun afterCommitExecutor(): AfterCommitExecutor = mock(AfterCommitExecutor::class.java)

    @Bean
    fun eventPublisher(): ApplicationEventPublisher = mock(ApplicationEventPublisher::class.java)

    @Bean
    fun externalEventPort(): ReservationExternalEventPort =
        mock(ReservationExternalEventPort::class.java)

    @Bean
    fun internalEventPort(): ReservationInternalEventPort =
        mock(ReservationInternalEventPort::class.java)
}
