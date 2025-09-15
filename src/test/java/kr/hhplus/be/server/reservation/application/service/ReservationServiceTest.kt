package kr.hhplus.be.server.reservation.application.service

import java.time.LocalDate
import java.util.UUID
import kr.hhplus.be.server.common.transactional.AfterCommitExecutor
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concertschedule.exception.ConcertScheduleException
import kr.hhplus.be.server.concertseat.domain.ConcertSeat
import kr.hhplus.be.server.concertseat.exception.ConcertSeatException
import kr.hhplus.be.server.fake.FakeConcertPort
import kr.hhplus.be.server.fake.FakeConcertSchedulePort
import kr.hhplus.be.server.fake.FakeConcertSeatPort
import kr.hhplus.be.server.fake.FakePaymentPort
import kr.hhplus.be.server.fake.FakeReservationExternalEventPort
import kr.hhplus.be.server.fake.FakeReservationPort
import kr.hhplus.be.server.fake.FakeRunner
import kr.hhplus.be.server.fake.FakeSeatPort
import kr.hhplus.be.server.reservation.adapter.input.web.response.MakeReservationResponse
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ReservationServiceTest {
    private lateinit var seatPort: FakeSeatPort
    private lateinit var concertPort: FakeConcertPort
    private lateinit var reservationPort: FakeReservationPort
    private lateinit var concertSeatPort: FakeConcertSeatPort
    private lateinit var reservationService: ReservationService
    private lateinit var concertSchedulePort: FakeConcertSchedulePort
    private lateinit var paymentPort: FakePaymentPort
    private lateinit var reservationContextLoader: ReservationContextLoader

    @BeforeEach
    fun setUp() {
        seatPort = FakeSeatPort()
        concertPort = FakeConcertPort()
        reservationPort = FakeReservationPort()
        concertSeatPort = FakeConcertSeatPort(seatPort)
        concertSchedulePort = FakeConcertSchedulePort(concertSeatPort)
        paymentPort = FakePaymentPort()
        reservationContextLoader =
            ReservationContextLoader(
                seatPort,
                concertSeatPort,
                concertSchedulePort
            )
        val transactional = Transactional(FakeRunner())
        reservationService =
            ReservationService(
                reservationPort = reservationPort,
                reservationContextLoader = reservationContextLoader,
                transactional = transactional,
                afterCommitExecutor = AfterCommitExecutor(),
                reservationEventPort = FakeReservationExternalEventPort()
            )
    }

    @Nested
    @DisplayName("콘서트 예약 서비스 테스트")
    inner class ConcertReservationServiceTest {
        @Nested
        @DisplayName("콘서트 예약 생성 테스트")
        inner class CreateReservationTest {
            @Nested
            @DisplayName("콘서트 예약 생성 성공 테스트")
            inner class CreateReservationSuccessTest {
                @Test
                @DisplayName("콘서트 좌석을 예약 할 수 있어야 한다.")
                fun reservationTest() {
                    // given
                    val date: LocalDate = LocalDate.now()
                    val concertSeatId = 1L
                    val userId: String = UUID.randomUUID().toString()
                    val concertId = 1L

                    seatPort.saveSingleSeat()
                    concertPort.saveSingleConcert(concertId)
                    concertSeatPort.saveSingleSeat(concertId)
                    concertSchedulePort.saveSingleSchedule(concertId)

                    // when
                    val actual: MakeReservationResponse =
                        reservationService.makeReservation(date, concertSeatId, userId)

                    // then
                    Assertions.assertThat(actual).isNotNull
                    Assertions.assertThat(actual.userId).isEqualTo(userId)
                    Assertions.assertThat(actual.concertSeatId).isEqualTo(concertSeatId)
                }
            }

            @Nested
            @DisplayName("콘서트 예약 생성 실패 테스트")
            inner class CreateReservationFailTest {
                @Test
                @DisplayName("콘서트 좌석 예약 시 잘못된 좌석으로 예약하는 경우 예외가 발생해야 한다.")
                fun reservationTest() {
                    // given
                    val date: LocalDate = LocalDate.now()
                    val concertSeatId = 999L
                    val userId: String = UUID.randomUUID().toString()
                    val concertId = 1L

                    seatPort.saveSingleSeat()
                    concertPort.saveSingleConcert(concertId)
                    concertSeatPort.saveSingleSeat(concertId)
                    concertSchedulePort.saveSingleSchedule(concertId)

                    // when & then
                    Assertions
                        .assertThatThrownBy {
                            reservationService.makeReservation(date, concertSeatId, userId)
                        }.isInstanceOf(ConcertSeatException::class.java)
                        .message()
                        .isEqualTo("not found concert seat.")
                }

                @Test
                @DisplayName("콘서트 좌석 예약 시 잘못된 날짜로 예약하는 경우 예외가 발생해야 한다.")
                fun reservationTest2() {
                    // given
                    val date: LocalDate = LocalDate.now().plusDays(10)
                    val concertSeatId = 1L
                    val userId: String = UUID.randomUUID().toString()
                    val concertId = 1L

                    seatPort.saveSingleSeat()
                    concertPort.saveSingleConcert(concertId)
                    concertSeatPort.saveSingleSeat(concertId)
                    concertSchedulePort.saveSingleSchedule(concertId)

                    // when & then
                    Assertions
                        .assertThatThrownBy {
                            reservationService.makeReservation(date, concertSeatId, userId)
                        }.isInstanceOf(ConcertScheduleException::class.java)
                        .message()
                        .isEqualTo("invalid concert date.")
                }

                @Test
                @DisplayName("콘서트 좌석 예약 시 이미 예약 중인 좌석을 예약하는 경우 예외가 발생해야 한다.")
                fun reservationTest3() {
                    // given
                    val date: LocalDate = LocalDate.now()
                    val concertSeatId = 1L
                    val userId: String = UUID.randomUUID().toString()
                    val concertId = 1L

                    seatPort.saveSingleSeat()
                    concertPort.saveSingleConcert(concertId)
                    concertSeatPort.saveSingleSeat(concertId, status = ConcertSeat.SeatStatus.HELD)
                    concertSchedulePort.saveSingleSchedule(concertId)

                    // when & then
                    Assertions
                        .assertThatThrownBy {
                            reservationService.makeReservation(date, concertSeatId, userId)
                        }.isInstanceOf(ConcertSeatException::class.java)
                        .message()
                        .isEqualTo("already reserved.")
                }
            }
        }
    }
}
