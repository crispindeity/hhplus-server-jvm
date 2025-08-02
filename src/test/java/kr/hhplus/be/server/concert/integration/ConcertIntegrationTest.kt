package kr.hhplus.be.server.concert.integration

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import kr.hhplus.be.server.common.integration.AbstractIntegrationTest
import kr.hhplus.be.server.fixture.UserFixture
import kr.hhplus.be.server.steps.ConcertSteps
import kr.hhplus.be.server.steps.EntryQueueTokenSteps
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class ConcertIntegrationTest : AbstractIntegrationTest() {
    @Nested
    @DisplayName("콘서트 통합 테스트")
    inner class ConcertIntegrationTest {
        @Nested
        @DisplayName("콘서트 예약 가능 날짜 조회 테스트")
        inner class FindAvailableDateTest {
            @Nested
            @DisplayName("콘서트 에약 가능 날짜 조회 성공 테스트")
            inner class FindAvailableDateSuccessTest {
                @Test
                @DisplayName("콘서트 예약 가능 날짜를 조회할 수 있어야 한다.")
                fun concertTest() {
                    // given
                    val concertId = 1L
                    val userId: String = UserFixture.getUserId()
                    val token: String = EntryQueueTokenSteps.getEntryQueueToken(userId)

                    // when
                    val response: Response =
                        Given {
                            header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            header("EntryQueueToken", token)
                            pathParam("id", concertId)
                        } When {
                            get("/api/concerts/{id}/reservations/available-dates")
                        } Extract {
                            response()
                        }

                    // then
                    SoftAssertions.assertSoftly { softly ->
                        softly.assertThat(response.jsonPath().getInt("code")).isEqualTo(200)
                        softly.assertThat(
                            response.jsonPath().getList<String>("result.dates").isNotEmpty()
                        )
                    }
                }
            }
        }

        @Nested
        @DisplayName("콘서트 예약 가능 좌석 조회 테스트")
        inner class FindAvailableSeatTest {
            @Nested
            @DisplayName("콘서트 예약 가능 좌석 조회 성공 테스트")
            inner class FindAvailableSeatSuccessTest {
                @Test
                @DisplayName("콘서트 예약 가능 좌석을 조회 할 수 있어야 한다.")
                fun concertTest() {
                    // given
                    val concertId = 1L
                    val userId: String = UserFixture.getUserId()
                    val token: String = EntryQueueTokenSteps.getEntryQueueToken(userId)
                    val dates: List<String> = ConcertSteps.getAvailableDates(concertId, token)

                    // when
                    val response: Response =
                        Given {
                            header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            header("EntryQueueToken", token)
                            param("date", dates.first())
                            pathParam("id", concertId)
                        } When {
                            get("/api/concerts/{id}/reservations/available-seats")
                        } Extract {
                            response()
                        }

                    // then
                    SoftAssertions.assertSoftly { softly ->
                        softly.assertThat(response.jsonPath().getInt("code")).isEqualTo(200)
                        softly.assertThat(
                            response.jsonPath().getList<String>("result.seats").isNotEmpty()
                        )
                    }
                }
            }
        }
    }
}
