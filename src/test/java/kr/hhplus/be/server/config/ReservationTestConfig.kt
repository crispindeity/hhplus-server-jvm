package kr.hhplus.be.server.config

import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concertschedule.application.port.ConcertSchedulePort
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.payment.application.port.PaymentPort
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.reservation.application.service.ReservationContextLoader
import kr.hhplus.be.server.reservation.application.service.ReservationService
import kr.hhplus.be.server.seat.application.port.SeatPort
import kr.hhplus.be.server.seathold.application.port.SeatHoldPort
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
internal class ReservationTestConfig {
    @Bean
    fun reservationService(
        seatHoldPort: SeatHoldPort,
        concertSeatPort: ConcertSeatPort,
        reservationPort: ReservationPort,
        paymentPort: PaymentPort,
        reservationContextLoader: ReservationContextLoader,
        transactional: Transactional
    ): ReservationService =
        ReservationService(
            seatHoldPort,
            concertSeatPort,
            reservationPort,
            paymentPort,
            reservationContextLoader,
            transactional
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
}
