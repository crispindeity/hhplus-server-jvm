package kr.hhplus.be.server.concert.application.service

import java.time.LocalDate
import kr.hhplus.be.server.concert.adapter.web.response.FindAvailableDatesResponse
import kr.hhplus.be.server.concert.adapter.web.response.FindAvailableSeatsResponses
import kr.hhplus.be.server.concert.exception.ConcertException
import kr.hhplus.be.server.fake.FakeConcertPort
import kr.hhplus.be.server.fake.FakeConcertSchedulePort
import kr.hhplus.be.server.fake.FakeConcertSeatPort
import kr.hhplus.be.server.fake.FakeSeatPort
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ConcertServiceTest {
    private lateinit var concertService: ConcertService
    private lateinit var concertPort: FakeConcertPort
    private lateinit var concertSchedulePort: FakeConcertSchedulePort
    private lateinit var concertSeatPort: FakeConcertSeatPort
    private lateinit var seatPort: FakeSeatPort

    @BeforeEach
    fun setUp() {
        concertPort = FakeConcertPort()
        seatPort = FakeSeatPort()
        concertSeatPort = FakeConcertSeatPort(seatPort)
        concertSchedulePort = FakeConcertSchedulePort(concertSeatPort)
        concertService =
            ConcertService(
                concertPort = concertPort,
                concertSchedulePort = concertSchedulePort,
                concertSeatPort = concertSeatPort
            )
    }

    @Nested
    @DisplayName("콘서트 서비스 테스트")
    inner class ConcertServiceTest {
        @Nested
        @DisplayName("예약 가능한 날짜 조회 테스트")
        inner class GetAvailableDatesTest {
            @Nested
            @DisplayName("예약 가능 날짜 조회 성공 테스트")
            inner class GetAvailableDatesSuccessTest {
                @Test
                @DisplayName("예약 가능한 콘서트 날짜를 조회할 수 있어야 한다.")
                fun concertDatesTest() {
                    // given
                    val concertId = 1L
                    concertPort.saveSingleConcert(concertId)
                    concertSchedulePort.saveSingleSchedule(concertId)
                    concertSeatPort.saveSingleSeat(concertId)
                    seatPort.saveSingleSeat()

                    // when
                    val actual: FindAvailableDatesResponse =
                        concertService.getAvailableDates(concertId)

                    // then
                    Assertions.assertThat(actual).isNotNull
                    Assertions.assertThat(actual.dates.size).isNotZero
                }
            }

            @Nested
            @DisplayName("에약 가능 날짜 조회 실패 테스트")
            inner class GetAvailableDatesFailTest {
                @Test
                @DisplayName("존재하지 않는 콘서트 아이디로 예약 가능 날짜 조회 시 예외가 발생해야 한다.")
                fun concertDatesTest() {
                    // given
                    val concertId = 1L

                    // when & then
                    Assertions
                        .assertThatThrownBy {
                            concertService.getAvailableDates(concertId)
                        }.isInstanceOf(ConcertException::class.java)
                        .message()
                        .isEqualTo("not found concert.")
                }
            }
        }

        @Nested
        @DisplayName("예약 가능한 좌석 조회 테스트")
        inner class GetAvailableSeatsTest {
            @Nested
            @DisplayName("예약 가능 좌석 조회 성공 테스트")
            inner class GetAvailableSeatsSuccessTest {
                @Test
                @DisplayName("예약 가능한 콘서트 좌석을 조회할 수 있어야 한다.")
                fun concertDatesTest() {
                    // given
                    val concertId = 1L
                    val date: LocalDate = LocalDate.now()
                    concertPort.saveSingleConcert(concertId)
                    concertSchedulePort.saveSingleSchedule(concertId)
                    concertSeatPort.saveSingleSeat(concertId)
                    seatPort.saveSingleSeat()

                    // when
                    val actual: FindAvailableSeatsResponses =
                        concertService.getAvailableSeats(concertId, date)

                    // then
                    Assertions.assertThat(actual).isNotNull
                    Assertions.assertThat(actual.seats.size).isNotZero
                }
            }

            @Nested
            @DisplayName("예약 가능 좌석 조회 실패 테스트")
            inner class GetAvailableSeatsFailTest {
                @Test
                @DisplayName("존재하지 않는 콘서트 아이디로 예약 가능 좌석 조회 시 예외가 발생해야 한다.")
                fun concertDatesTest() {
                    // given
                    val concertId = 1L

                    // when & then
                    Assertions
                        .assertThatThrownBy {
                            concertService.getAvailableDates(concertId)
                        }.isInstanceOf(ConcertException::class.java)
                        .message()
                        .isEqualTo("not found concert.")
                }
            }
        }
    }
}
