package kr.hhplus.be.server.config

import kr.hhplus.be.server.concert.application.port.ConcertPort
import kr.hhplus.be.server.concert.application.service.ConcertService
import kr.hhplus.be.server.concertschedule.application.port.ConcertSchedulePort
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.payment.application.port.PaymentPort
import kr.hhplus.be.server.payment.application.service.PaymentService
import kr.hhplus.be.server.pointtransaction.application.port.PointTransactionPort
import kr.hhplus.be.server.pointwallet.application.port.PointWalletPort
import kr.hhplus.be.server.pointwallet.application.service.PointWalletService
import kr.hhplus.be.server.queuetoken.application.port.EntryQueuePort
import kr.hhplus.be.server.queuetoken.application.service.EntryQueueService
import kr.hhplus.be.server.queuetoken.application.service.JWTHelper
import kr.hhplus.be.server.queuetoken.application.service.QueueAccessValidator
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.reservation.application.service.ReservationContextLoader
import kr.hhplus.be.server.reservation.application.service.ReservationService
import kr.hhplus.be.server.seat.application.port.SeatPort
import kr.hhplus.be.server.seathold.application.port.SeatHoldPort
import kr.hhplus.be.server.user.application.port.UserPort
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
    fun pointTransactionPort(): PointTransactionPort = mock(PointTransactionPort::class.java)

    @Bean
    fun userPointService(
        userPort: UserPort,
        pointWalletPort: PointWalletPort,
        pointTransactionPort: PointTransactionPort
    ): PointWalletService =
        PointWalletService(
            userPort,
            pointWalletPort,
            pointTransactionPort
        )

    @Bean
    fun seatPort(): SeatPort = mock(SeatPort::class.java)

    @Bean
    fun paymentService(
        paymentPort: PaymentPort,
        reservationPort: ReservationPort,
        pointWalletPort: PointWalletPort,
        concertSeatPort: ConcertSeatPort,
        entryQueuePort: EntryQueuePort,
        seatHoldPort: SeatHoldPort,
        pointTransactionPort: PointTransactionPort
    ): PaymentService =
        PaymentService(
            paymentPort,
            reservationPort,
            pointWalletPort,
            concertSeatPort,
            entryQueuePort,
            seatHoldPort,
            pointTransactionPort
        )
}
