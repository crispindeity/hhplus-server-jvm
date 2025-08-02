package kr.hhplus.be.server.queuetoken.integration

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import kr.hhplus.be.server.common.integration.AbstractIntegrationTest
import kr.hhplus.be.server.fixture.UserFixture
import kr.hhplus.be.server.queuetoken.adapter.web.request.EntryQueueTokenRequest
import kr.hhplus.be.server.queuetoken.application.service.JWTHelper
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

class QueueTokenIntegrationTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var jwtHelper: JWTHelper

    @Nested
    @DisplayName("대기열 토큰 통합 테스트")
    inner class EntryQueueTokenIntegrationTest {
        @Nested
        @DisplayName("대기열 토큰 발급 테스트")
        inner class GetEntryQueueTokenTest {
            @Nested
            @DisplayName("대기열 토큰 발급 성공 테스트")
            inner class GetEntryQueueTokenSuccessTest {
                @Test
                @DisplayName("대기열 토큰을 받을 받을 수 있여야 한다.")
                fun entryQueueTokenTest() {
                    // given
                    val userId: String = UserFixture.getUserId()
                    val request = EntryQueueTokenRequest(userId)

                    // when
                    val response: Response =
                        Given {
                            header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            body(request)
                        } When {
                            post("/api/queue/entry-token")
                        } Extract {
                            response()
                        }

                    // then
                    val tokenHeader: String = response.header("EntryQueueToken")

                    SoftAssertions.assertSoftly { softly ->
                        softly.assertThat(response.jsonPath().getInt("code")).isEqualTo(200)
                        softly.assertThat(tokenHeader).isNotNull
                        softly
                            .assertThat(
                                jwtHelper.parseJWT(tokenHeader).getIntegerClaim("queueNumber")
                            ).isEqualTo(1)
                    }
                }
            }
        }
    }
}
