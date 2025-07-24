package kr.hhplus.be.server.config

import kr.hhplus.be.server.application.port.ConcertPort
import kr.hhplus.be.server.application.port.ConcertSchedulePort
import kr.hhplus.be.server.application.port.ConcertSeatPort
import kr.hhplus.be.server.application.port.EntryQueuePort
import kr.hhplus.be.server.application.port.PaymentPort
import kr.hhplus.be.server.application.port.PointWalletPort
import kr.hhplus.be.server.application.port.ReservationPort
import kr.hhplus.be.server.application.port.SeatHoldPort
import kr.hhplus.be.server.application.port.SeatPort
import kr.hhplus.be.server.application.port.UserPort
import kr.hhplus.be.server.application.service.ConcertService
import kr.hhplus.be.server.application.service.EntryQueueService
import kr.hhplus.be.server.application.service.JWTHelper
import kr.hhplus.be.server.application.service.QueueAccessValidator
import kr.hhplus.be.server.application.service.ReservationContextLoader
import kr.hhplus.be.server.application.service.ReservationService
import kr.hhplus.be.server.application.service.UserPointService
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
internal class TestConfig {
    @Bean
    fun entryQueuePort(): EntryQueuePort = mock(EntryQueuePort::class.java)

    @Bean
    fun queueAccessValidator(entryQueuePort: EntryQueuePort): QueueAccessValidator =
        QueueAccessValidator(entryQueuePort)

    @Bean
    fun jwtHelper(): JWTHelper = mock()

    @Bean
    fun entryQueueService(
        entryQueuePort: EntryQueuePort,
        jwtHelper: JWTHelper
    ): EntryQueueService = EntryQueueService(entryQueuePort, jwtHelper)

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
        concertSeatPort: ConcertSeatPort
    ): ConcertService = ConcertService(concertPort, concertSchedulePort, concertSeatPort)

    @Bean
    fun seatHoldPort(): SeatHoldPort = mock(SeatHoldPort::class.java)

    @Bean
    fun reservationPort(): ReservationPort = mock(ReservationPort::class.java)

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
    fun paymentPort(): PaymentPort = mock(PaymentPort::class.java)

    @Bean
    fun reservationService(
        seatHoldPort: SeatHoldPort,
        concertSeatPort: ConcertSeatPort,
        reservationPort: ReservationPort,
        paymentPort: PaymentPort,
        reservationContextLoader: ReservationContextLoader
    ): ReservationService =
        ReservationService(
            seatHoldPort,
            concertSeatPort,
            reservationPort,
            paymentPort,
            reservationContextLoader
        )

    @Bean
    fun userPort(): UserPort = mock(UserPort::class.java)

    @Bean
    fun pointWalletPort(): PointWalletPort = mock(PointWalletPort::class.java)

    @Bean
    fun userPointService(
        userPort: UserPort,
        pointWalletPort: PointWalletPort
    ): UserPointService =
        UserPointService(
            userPort,
            pointWalletPort
        )

    @Bean
    fun seatPort(): SeatPort = mock(SeatPort::class.java)
}
