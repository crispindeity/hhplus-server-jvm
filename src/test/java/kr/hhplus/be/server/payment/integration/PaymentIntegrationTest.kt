package kr.hhplus.be.server.payment.integration

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import kr.hhplus.be.server.common.integration.AbstractIntegrationTest
import kr.hhplus.be.server.fixture.UserFixture
import kr.hhplus.be.server.steps.EntryQueueTokenSteps
import kr.hhplus.be.server.steps.PointWalletSteps
import kr.hhplus.be.server.steps.ReservationSteps
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class PaymentIntegrationTest : AbstractIntegrationTest() {
    @Nested
    @DisplayName("결제 통합 테스트")
    inner class PaymentIntegrationTest {
        @Nested
        @DisplayName("결제 진행 테스트")
        inner class PaymentTest {
            @Nested
            @DisplayName("결제 진행 성공 테스트")
            inner class PaymentSuccessTest {
                @Test
                @DisplayName("생성된 예약에 대해 결제를 할 수 있어야 한다.")
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
                    val response: Response =
                        Given {
                            header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            header("EntryQueueToken", token)
                        } When {
                            post("/api/payments")
                        } Then {
                            log().all()
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
