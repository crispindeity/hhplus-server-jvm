package kr.hhplus.be.server.queuetoken.concurrency

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.ValidatableResponse
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kr.hhplus.be.server.common.integration.AbstractIntegrationTest
import kr.hhplus.be.server.queuetoken.adapter.web.request.EntryQueueTokenRequest
import kr.hhplus.be.server.queuetoken.application.port.EntryQueuePort
import org.assertj.core.api.SoftAssertions
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("concurrency-test")
class EntryQueueConcurrencyTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var entryQueuePort: EntryQueuePort

    @Nested
    @DisplayName("대기열 토큰 동시성 테스트")
    inner class EntryQueueConcurrencyTest {
        @Nested
        @DisplayName("대기열 토큰 발급 동시성 테스트")
        inner class GetEntryQueueConcurrencyTest {
            @Nested
            @DisplayName("대기열 토큰 동시성 처리 성공 테스트")
            inner class GetEntryQueueConcurrencySuccessTest {
                @Test
                @DisplayName("모든 대기열 토큰 발급 이후 다음 토큰의 순번은 101번이어야 한다.")
                fun entryQueueToken() {
                    // given & when
                    val futures: List<CompletableFuture<ValidatableResponse>> =
                        (1..100).map {
                            val userId: String = UUID.randomUUID().toString()
                            val request = EntryQueueTokenRequest(userId)
                            CompletableFuture.supplyAsync {
                                try {
                                    Given {
                                        header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        body(request)
                                    } When {
                                        post("/api/queue/entry-token")
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

                    SoftAssertions.assertSoftly { softly ->
                        softly.assertThat(responses).hasSize(100)
                        softly
                            .assertThat(entryQueuePort.getEntryQueueNextNumber())
                            .isEqualTo(101)
                    }
                }
            }
        }
    }
}
