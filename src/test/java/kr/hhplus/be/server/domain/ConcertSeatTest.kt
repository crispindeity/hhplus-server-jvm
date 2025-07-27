package kr.hhplus.be.server.domain

import kr.hhplus.be.server.common.exception.CustomException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ConcertSeatTest {
    @Nested
    @DisplayName("콘서트 좌석 테스트")
    inner class ConcertSeatTest {
        @Nested
        @DisplayName("콘서트 좌석 상태 변경 테스트")
        inner class ConcertSeatChangeStatusTest {
            @Nested
            @DisplayName("콘서트 좌석 상태 변경 성공 테스트")
            inner class ConcertSeatChangeStatusSuccessTest {
                @Test
                @DisplayName("콘서트 좌석의 상태를 Held 로 변경할 수 있어야 한다.")
                fun concertSeatTest() {
                    // given
                    val availableSeat =
                        ConcertSeat(
                            id = 1L,
                            scheduleId = 1L,
                            seatId = 1L,
                            status = ConcertSeat.SeatStatus.AVAILABLE
                        )

                    // when
                    val actual: ConcertSeat = availableSeat.held()

                    // then
                    Assertions.assertThat(actual.status).isEqualTo(ConcertSeat.SeatStatus.HELD)
                }
            }

            @Nested
            @DisplayName("콘서트 좌석 상태 변경 실패 테스트")
            inner class ConcertSeatChangeStatusFailTest {
                @Test
                @DisplayName("콘서트 좌석의 상태를 Held 로 변경 시 현재 상태가 AVAILABLE 이 아닌 경우 예외가 발생해야 한다.")
                fun concertSeatTest() {
                    // given
                    val availableSeat =
                        ConcertSeat(
                            id = 1L,
                            scheduleId = 1L,
                            seatId = 1L,
                            status = ConcertSeat.SeatStatus.RESERVED
                        )

                    // when & then
                    Assertions
                        .assertThatThrownBy { availableSeat.held() }
                        .isInstanceOf(
                            CustomException::class.java
                        ).message()
                        .isEqualTo("already reserved.")
                }
            }
        }
    }
}
