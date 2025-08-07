package kr.hhplus.be.server.payment.concurrency

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.ValidatableResponse
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kr.hhplus.be.server.common.integration.AbstractIntegrationTest
import kr.hhplus.be.server.fixture.UserFixture
import kr.hhplus.be.server.steps.EntryQueueTokenSteps
import kr.hhplus.be.server.steps.PointWalletSteps
import kr.hhplus.be.server.steps.ReservationSteps
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles

@Tag("concurrency")
@ActiveProfiles("concurrency-test")
class PaymentConcurrencyTest : AbstractIntegrationTest() {
    @Nested
    @DisplayName("결제 동시성 테스트")
    inner class PaymentConcurrencyTest {
        @Nested
        @DisplayName("결제 진행 동시성 테스트")
        inner class PaymentConcurrencyTest {
            @Nested
            @DisplayName("결제 진행 동시성 처리 성공 테스트")
            inner class PaymentConcurrencySuccessTest {
                @Test
                @DisplayName("하나의 예약에 동시에 결제 요청이 발생하는 경우 최초 요청만 성공해야 한다.")
                fun paymentTest() {
                    // given
                    val concertId = 1L
                    val userId: String = UserFixture.getUserId()
                    val token: String = EntryQueueTokenSteps.getEntryQueueToken(userId)
                    ReservationSteps.makeReservation(concertId, token)
                    PointWalletSteps.chargePoint(
                        userId = userId,
                        amount = 10000L,
                        token = token
                    )

                    // when
                    val futures: List<CompletableFuture<ValidatableResponse>> =
                        (1..10).map {
                            CompletableFuture.supplyAsync {
                                try {
                                    Given {
                                        header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        header("EntryQueueToken", token)
                                    } When {
                                        post("/api/payments")
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
                    val paymentCount: Int = checkPaymentCount(responses)

                    SoftAssertions.assertSoftly { softly ->
                        softly.assertThat(responses).hasSize(10)
                        softly.assertThat(paymentCount).isEqualTo(1)
                    }
                }
            }
        }
    }

    private fun checkPaymentCount(responses: List<ValidatableResponse>): Int =
        responses
            .mapNotNull {
                try {
                    it.extract().jsonPath().getLong("result.reservationCount")
                } catch (_: Exception) {
                    null
                }
            }.count()
}
