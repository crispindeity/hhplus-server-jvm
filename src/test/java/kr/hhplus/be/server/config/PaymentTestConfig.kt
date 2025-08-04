package kr.hhplus.be.server.config

import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.payment.application.port.PaymentPort
import kr.hhplus.be.server.payment.application.service.PaymentService
import kr.hhplus.be.server.pointtransaction.application.port.PointTransactionPort
import kr.hhplus.be.server.pointwallet.application.port.PointWalletPort
import kr.hhplus.be.server.queuetoken.application.port.EntryQueuePort
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.seathold.application.port.SeatHoldPort
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
internal class PaymentTestConfig {
    @Bean
    fun paymentPort(): PaymentPort = mock(PaymentPort::class.java)

    @Bean
    fun paymentService(
        paymentPort: PaymentPort,
        reservationPort: ReservationPort,
        pointWalletPort: PointWalletPort,
        concertSeatPort: ConcertSeatPort,
        entryQueuePort: EntryQueuePort,
        seatHoldPort: SeatHoldPort,
        pointTransactionPort: PointTransactionPort,
        transactional: Transactional
    ): PaymentService =
        PaymentService(
            paymentPort,
            reservationPort,
            pointWalletPort,
            concertSeatPort,
            entryQueuePort,
            seatHoldPort,
            pointTransactionPort,
            transactional
        )
}
