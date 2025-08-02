package kr.hhplus.be.server.pointwallet.integration

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import kr.hhplus.be.server.common.integration.AbstractIntegrationTest
import kr.hhplus.be.server.fixture.UserFixture
import kr.hhplus.be.server.pointwallet.adapter.web.request.ChargePointsRequest
import kr.hhplus.be.server.steps.EntryQueueTokenSteps
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class PointWalletIntegrationTest : AbstractIntegrationTest() {
    @Nested
    @DisplayName("포인트 지갑 통합 테스트")
    inner class PointWalletTest {
        @Nested
        @DisplayName("포인트 충전 테스트")
        inner class ChargePointTest {
            @Nested
            @DisplayName("포인트 충전 성공 테스트")
            inner class ChargePointSuccessTest {
                @Test
                @DisplayName("포인트를 충전 할 수 있어야 한다.")
                fun pointWalletTest() {
                    // given
                    val request = ChargePointsRequest(1000L)
                    val userId: String = UserFixture.getUserId()
                    val token: String = EntryQueueTokenSteps.getEntryQueueToken(userId)

                    // when
                    val response: Response =
                        Given {
                            header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            header("EntryQueueToken", token)
                            body(request)
                            pathParam("id", userId)
                        } When {
                            post("/api/users/{id}/points/charge")
                        } Extract {
                            response()
                        }

                    // then
                    SoftAssertions.assertSoftly { softly ->
                        softly.assertThat(response.jsonPath().getInt("code")).isEqualTo(200)
                        softly.assertThat(response.jsonPath().getInt("result")).isEqualTo(1000)
                    }
                }
            }
        }

        @Nested
        @DisplayName("포인트 조회 테스트")
        inner class FindPointTest {
            @Nested
            @DisplayName("포인트 조회 성공 테스트")
            inner class FindPointSuccessTest {
                @Test
                @DisplayName("보유한 포인트를 조회 할 수 있어야 한다.")
                fun pointWalletTest() {
                    // given
                    val userId: String = UserFixture.getUserId()
                    val token: String = EntryQueueTokenSteps.getEntryQueueToken(userId)

                    // when
                    val response: Response =
                        Given {
                            header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            header("EntryQueueToken", token)
                            pathParam("id", userId)
                        } When {
                            get("/api/users/{id}/points")
                        } Extract {
                            response()
                        }

                    // then
                    SoftAssertions.assertSoftly { softly ->
                        softly
                            .assertThat(response.jsonPath().getInt("code"))
                            .isEqualTo(200)
                        softly
                            .assertThat(response.jsonPath().getString("result.userId"))
                            .isEqualTo(userId)
                    }
                }
            }
        }
    }
}
