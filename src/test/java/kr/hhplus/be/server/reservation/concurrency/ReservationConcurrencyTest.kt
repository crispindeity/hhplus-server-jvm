package kr.hhplus.be.server.reservation.concurrency

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.ValidatableResponse
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kr.hhplus.be.server.common.integration.AbstractIntegrationTest
import kr.hhplus.be.server.fixture.UserFixture
import kr.hhplus.be.server.reservation.adapter.input.web.request.MakeReservationRequest
import kr.hhplus.be.server.steps.ConcertSteps
import kr.hhplus.be.server.steps.EntryQueueTokenSteps
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles

@Tag("concurrency")
@ActiveProfiles("concurrency-test")
class ReservationConcurrencyTest : AbstractIntegrationTest() {
    @Nested
    @DisplayName("예약 동시성 테스트")
    inner class ReservationConcurrencyTest {
        @Nested
        @DisplayName("예약 생성 동시성 테스트")
        inner class MakeReservationConcurrencyTest {
            @Nested
            @DisplayName("예약 생성 동시성 처리 성공 테스트")
            inner class MakeReservationConcurrencySuccessTest {
                @Test
                @DisplayName("동일한 좌석에 대한 예약 생성 요청 시 최초 요청만 성공하고, 그 이후 요청은 실패해야 한다.")
                fun makeReservationTest() {
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
                    val futures: List<CompletableFuture<ValidatableResponse>> =
                        (1..10).map {
                            val userId: String = UUID.randomUUID().toString()
                            val token: String = EntryQueueTokenSteps.getEntryQueueToken(userId)
                            CompletableFuture.supplyAsync {
                                try {
                                    Given {
                                        header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        header("EntryQueueToken", token)
                                        body(request)
                                    } When {
                                        post("/api/reservations")
                                    } Then {
                                        statusCode(200)
                                    }
                                } catch (exception: Exception) {
                                    throw exception
                                }
                            }
                        }

                    // then
                    val responses: List<ValidatableResponse> =
                        futures.map { it.get(10, TimeUnit.SECONDS) }
                    val reservationCount: Int = checkReservationCount(responses)
                    val duplicatedConcertSeatIds: Set<Long> =
                        checkDuplicatedConcertSeats(responses)

                    SoftAssertions.assertSoftly { softly ->
                        softly.assertThat(responses).hasSize(10)
                        softly.assertThat(reservationCount).isEqualTo(1)
                        softly.assertThat(duplicatedConcertSeatIds).isEmpty()
                    }
                }
            }
        }
    }

    private fun checkReservationCount(responses: List<ValidatableResponse>): Int =
        responses
            .mapNotNull {
                try {
                    it.extract().jsonPath().getLong("result.concertSeatId")
                } catch (_: Exception) {
                    null
                }
            }.count()

    private fun checkDuplicatedConcertSeats(responses: List<ValidatableResponse>): Set<Long> {
        val duplicatedConcertSeatIds: Set<Long> =
            responses
                .mapNotNull {
                    try {
                        it.extract().jsonPath().getLong("result.concertSeatId")
                    } catch (_: Exception) {
                        null
                    }
                }.groupingBy { it }
                .eachCount()
                .filter { it.value > 1 }
                .keys
        return duplicatedConcertSeatIds
    }
}
