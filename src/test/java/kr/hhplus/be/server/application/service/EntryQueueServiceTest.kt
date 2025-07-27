package kr.hhplus.be.server.application.service

import java.util.UUID
import kr.hhplus.be.server.common.exception.QueueTokenException
import kr.hhplus.be.server.config.jwt.JWTProperties
import kr.hhplus.be.server.fake.FakeEntryQueuePort
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EntryQueueServiceTest {
    private lateinit var entryQueueService: EntryQueueService
    private lateinit var entryQueuePort: FakeEntryQueuePort

    @BeforeEach
    fun setUp() {
        entryQueuePort = FakeEntryQueuePort()
        entryQueueService =
            EntryQueueService(
                entryQueuePort = entryQueuePort,
                JWTHelper(
                    JWTProperties(
                        secret = "uLzJXp4n5z9J9fV4p8VJdRhBqBv9c8r+O7dnYGfH1V4=",
                        expirationMinutes = 60
                    )
                )
            )
    }

    @Nested
    @DisplayName("대기열 서비스 테스트")
    inner class EntryQueueServiceTest {
        @Nested
        @DisplayName("대기열 토큰 발급 테스트")
        inner class EntryQueueTokenTest {
            @Nested
            @DisplayName("대기열 토큰 발급 성공 테스트")
            inner class EntryQueueTokenSuccessTest {
                @Test
                @DisplayName("대기열 토큰을 발급 받을 수 있어야 한다.")
                fun createEntryQueueTokenSuccessTest() {
                    // given
                    val userId: UUID = UUID.randomUUID()

                    // when
                    val actual: String = entryQueueService.createEntryQueueToken(userId)

                    // then
                    Assertions.assertThat(actual).isNotNull
                }
            }

            @Nested
            @DisplayName("대기열 토큰 발급 실패 테스트")
            inner class EntryQueueTokenFailTest {
                @Test
                @DisplayName("토큰 발급 시 이미 Waiting 상태의 토큰이 발급 되어 있다면 예외가 발생해야 한다.")
                fun createEntryQueueTokenFailTest() {
                    // given
                    val userId: UUID = UUID.fromString("9a0f5d10-b26c-4f5d-909b-d1594f8cfa51")
                    entryQueuePort.saveSingleQueueToken(userId)

                    // when & then
                    Assertions
                        .assertThatThrownBy {
                            entryQueueService.createEntryQueueToken(
                                userId
                            )
                        }.isInstanceOf(QueueTokenException::class.java)
                        .message()
                        .isEqualTo("token already issued. - $userId")
                }
            }
        }
    }
}
