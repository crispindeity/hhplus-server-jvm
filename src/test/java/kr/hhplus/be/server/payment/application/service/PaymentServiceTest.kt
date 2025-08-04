package kr.hhplus.be.server.payment.application.service

import java.util.UUID
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concertseat.domain.ConcertSeat
import kr.hhplus.be.server.fake.FakeConcertSeatPort
import kr.hhplus.be.server.fake.FakeEntryQueuePort
import kr.hhplus.be.server.fake.FakePaymentPort
import kr.hhplus.be.server.fake.FakePointTransactionPort
import kr.hhplus.be.server.fake.FakePointWalletPort
import kr.hhplus.be.server.fake.FakeReservationPort
import kr.hhplus.be.server.fake.FakeRunner
import kr.hhplus.be.server.fake.FakeSeatHoldPort
import kr.hhplus.be.server.fake.FakeSeatPort
import kr.hhplus.be.server.payment.adapter.web.dto.response.PaymentResponse
import kr.hhplus.be.server.pointtransaction.application.port.PointTransactionPort
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PaymentServiceTest {
    private lateinit var paymentService: PaymentService
    private lateinit var paymentPort: FakePaymentPort
    private lateinit var reservationPort: FakeReservationPort
    private lateinit var seatHoldPort: FakeSeatHoldPort
    private lateinit var entryQueuePort: FakeEntryQueuePort
    private lateinit var pointWalletPort: FakePointWalletPort
    private lateinit var concertSeatPort: FakeConcertSeatPort
    private lateinit var seatPort: FakeSeatPort
    private lateinit var pointTransactionPort: PointTransactionPort

    @BeforeEach
    fun setUp() {
        seatPort = FakeSeatPort()
        paymentPort = FakePaymentPort()
        seatHoldPort = FakeSeatHoldPort()
        entryQueuePort = FakeEntryQueuePort()
        reservationPort = FakeReservationPort()
        pointWalletPort = FakePointWalletPort()
        concertSeatPort = FakeConcertSeatPort(seatPort)
        pointTransactionPort = FakePointTransactionPort()
        val transactional = Transactional(FakeRunner())
        paymentService =
            PaymentService(
                paymentPort = paymentPort,
                reservationPort = reservationPort,
                pointWalletPort = pointWalletPort,
                concertSeatPort = concertSeatPort,
                entryQueuePort = entryQueuePort,
                seatHoldPort = seatHoldPort,
                pointTransactionPort = pointTransactionPort,
                transactional = transactional
            )
    }

    @Nested
    @DisplayName("결제 서비스 테스트")
    inner class PaymentServiceTest {
        @Nested
        @DisplayName("결제 처리 테스트")
        inner class PaymentTest {
            @Nested
            @DisplayName("결제 처리 성공 테스트")
            inner class PaymentSuccessTest {
                @Test
                @DisplayName("예약한 콘서트에 대해 결제를 할 수 있어야 한다.")
                fun paymentTest() {
                    // given
                    val userId: UUID = UUID.randomUUID()
                    reservationPort.saveSingleReservation(userId)
                    pointWalletPort.saveSingleWallet(userId)
                    paymentPort.saveSinglePayment(userId)
                    concertSeatPort.saveSingleSeat(1L, ConcertSeat.SeatStatus.HELD)
                    entryQueuePort.saveSingleQueueToken(userId)

                    // when
                    val actual: PaymentResponse = paymentService.payment(userId.toString())

                    // then
                    Assertions.assertThat(actual.totalPrice).isEqualTo(1000L)
                }
            }
        }
    }
}
