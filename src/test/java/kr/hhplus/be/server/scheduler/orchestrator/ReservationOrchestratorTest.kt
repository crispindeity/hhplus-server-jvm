package kr.hhplus.be.server.scheduler.orchestrator

import java.time.LocalDateTime
import java.util.UUID
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.fake.FakeConcertSeatPort
import kr.hhplus.be.server.fake.FakePaymentPort
import kr.hhplus.be.server.fake.FakeReservationPort
import kr.hhplus.be.server.fake.FakeRunner
import kr.hhplus.be.server.fake.FakeSeatHoldPort
import kr.hhplus.be.server.fake.FakeSeatPort
import kr.hhplus.be.server.reservation.domain.Reservation
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ReservationOrchestratorTest {
    private lateinit var reservationOrchestrator: ReservationOrchestrator
    private lateinit var reservationPort: FakeReservationPort
    private lateinit var paymentPort: FakePaymentPort
    private lateinit var concertSeatPort: FakeConcertSeatPort
    private lateinit var seatHoldPort: FakeSeatHoldPort
    private lateinit var seatPort: FakeSeatPort

    @BeforeEach
    fun setUp() {
        seatPort = FakeSeatPort()
        reservationPort = FakeReservationPort()
        paymentPort = FakePaymentPort()
        concertSeatPort = FakeConcertSeatPort(seatPort)
        seatHoldPort = FakeSeatHoldPort()
        val transactional = Transactional(FakeRunner())
        reservationOrchestrator =
            ReservationOrchestrator(
                reservationPort,
                paymentPort,
                seatHoldPort,
                concertSeatPort,
                transactional
            )
    }

    @Nested
    @DisplayName("예약 오케스레이터 테스트")
    inner class ReservationOrchestratorTest {
        @Nested
        @DisplayName("예약 만료 테스트")
        inner class ExpireReservationTest {
            @Nested
            @DisplayName("예약 만료 성공 테스트")
            inner class ExpireReservationSuccessTest {
                @Test
                @DisplayName("만료 시간이 지난 예약에 대해 만료 처리를 할 수 있어야 한다.")
                fun expireTest() {
                    // given
                    val currentDateTime: LocalDateTime = LocalDateTime.now()
                    reservationPort.save(
                        Reservation(
                            userId = UUID.randomUUID(),
                            concertId = 1L,
                            concertSeatId = 1L,
                            paymentId = 1L,
                            reservedAt = LocalDateTime.now().minusMinutes(5),
                            expiresAt = currentDateTime,
                            status = Reservation.Status.IN_PROGRESS
                        )
                    )

                    // when & then
                    Assertions
                        .assertThatCode {
                            reservationOrchestrator.expireReservations(currentDateTime)
                        }.doesNotThrowAnyException()
                }
            }
        }
    }
}
