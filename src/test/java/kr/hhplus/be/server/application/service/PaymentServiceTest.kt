package kr.hhplus.be.server.application.service

import java.util.UUID
import kr.hhplus.be.server.adapter.web.dto.response.PaymentResponse
import kr.hhplus.be.server.domain.ConcertSeat
import kr.hhplus.be.server.fake.FakeConcertSeatPort
import kr.hhplus.be.server.fake.FakeEntryQueuePort
import kr.hhplus.be.server.fake.FakePaymentPort
import kr.hhplus.be.server.fake.FakePointWalletPort
import kr.hhplus.be.server.fake.FakeReservationPort
import kr.hhplus.be.server.fake.FakeSeatHoldPort
import kr.hhplus.be.server.fake.FakeSeatPort
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

    @BeforeEach
    fun setUp() {
        seatPort = FakeSeatPort()
        paymentPort = FakePaymentPort()
        seatHoldPort = FakeSeatHoldPort()
        entryQueuePort = FakeEntryQueuePort()
        reservationPort = FakeReservationPort()
        pointWalletPort = FakePointWalletPort()
        concertSeatPort = FakeConcertSeatPort(seatPort)
        paymentService =
            PaymentService(
                paymentPort = paymentPort,
                reservationPort = reservationPort,
                pointWalletPort = pointWalletPort,
                concertSeatPort = concertSeatPort,
                entryQueuePort = entryQueuePort,
                seatHoldPort = seatHoldPort
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
