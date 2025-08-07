package kr.hhplus.be.server.pointwallet.concurrency

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.ValidatableResponse
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kr.hhplus.be.server.common.integration.AbstractIntegrationTest
import kr.hhplus.be.server.fixture.UserFixture
import kr.hhplus.be.server.pointwallet.adapter.web.request.ChargePointsRequest
import kr.hhplus.be.server.pointwallet.application.port.PointWalletPort
import kr.hhplus.be.server.steps.EntryQueueTokenSteps
import org.assertj.core.api.SoftAssertions
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles

@Tag("concurrency")
@ActiveProfiles("concurrency-test")
class PointWalletConcurrencyTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var pointWalletPort: PointWalletPort

    @Nested
    @DisplayName("포인트 동시성 테스트")
    inner class PointWalletConcurrencyTest {
        @Nested
        @DisplayName("포인트 충전 동시성 테스트")
        inner class ChargePointConcurrencyTest {
            @Nested
            @DisplayName("포인트 충전 동시성 처리 성공 테스트")
            inner class ChargePointConcurrencySuccessTest {
                @Test
                @DisplayName("동시에 여러 포인트 충전 요청 시, 모든 요청에 대한 충전이 성공적으로 되야 한다.")
                fun chargePointTest() {
                    // given
                    val request = ChargePointsRequest(1000L)
                    val userId: String = UserFixture.getUserId()
                    val token: String = EntryQueueTokenSteps.getEntryQueueToken(userId)

                    // when
                    val futures: List<CompletableFuture<ValidatableResponse>> =
                        (1..100).map {
                            CompletableFuture.supplyAsync {
                                try {
                                    Given {
                                        header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        header("EntryQueueToken", token)
                                        body(request)
                                        pathParam("id", userId)
                                    } When {
                                        post("/api/users/{id}/points/charge")
                                    } Then {
                                        statusCode(200)
                                        body("code", Matchers.equalTo(200))
                                    }
                                } catch (exception: Exception) {
                                    throw exception
                                }
                            }
                        }

                    // then
                    val responses: List<ValidatableResponse> =
                        futures.map { it.get(10, TimeUnit.SECONDS) }
                    val balance: Long = pointWalletPort.getWallet(UUID.fromString(userId))!!.balance

                    SoftAssertions.assertSoftly { softly ->
                        softly.assertThat(responses).hasSize(100)
                        softly.assertThat(balance).isEqualTo(100_000L)
                    }
                }
            }
        }
    }
}
