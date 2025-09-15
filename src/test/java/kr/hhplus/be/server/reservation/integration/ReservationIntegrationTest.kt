package kr.hhplus.be.server.reservation.integration

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import java.time.LocalDate
import kr.hhplus.be.server.common.integration.AbstractIntegrationTest
import kr.hhplus.be.server.fixture.UserFixture
import kr.hhplus.be.server.reservation.adapter.input.web.request.MakeReservationRequest
import kr.hhplus.be.server.steps.ConcertSteps
import kr.hhplus.be.server.steps.EntryQueueTokenSteps
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class ReservationIntegrationTest : AbstractIntegrationTest() {
    @Nested
    @DisplayName("예약 통합 테스트")
    inner class ReservationIntegrationTest {
        @Nested
        @DisplayName("예약 생성 테스트")
        inner class MakeReservationTest {
            @Nested
            @DisplayName("예약 생성 성공 테스트")
            inner class MakeReservationSuccessTest {
                @Test
                @DisplayName("예약을 생성 할 수 있어야 한다.")
                fun reservationTest() {
                    // given
                    val concertId = 1L
                    val userId: String = UserFixture.getUserId()
                    val token: String = EntryQueueTokenSteps.getEntryQueueToken(userId)
                    val date: String = ConcertSteps.getAvailableDates(concertId, token).first()
                    val seat: Int = ConcertSteps.getAvailableSeats(concertId, token, date)

                    val request =
                        MakeReservationRequest(
                            date = LocalDate.parse(date),
                            seat = seat.toLong()
                        )

                    // when
                    val response: Response =
                        Given {
                            header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            header("EntryQueueToken", token)
                            body(request)
                        } When {
                            post("/api/reservations")
                        } Extract {
                            response()
                        }

                    // then
                    SoftAssertions.assertSoftly { softly ->
                        softly.assertThat(response.jsonPath().getInt("code")).isEqualTo(200)
                    }
                }
            }
        }
    }
}
