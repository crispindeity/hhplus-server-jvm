package kr.hhplus.be.server.application.service

import java.util.UUID
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.domain.QueueToken
import kr.hhplus.be.server.fake.FakeEntryQueuePort
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class QueueAccessValidatorTest {
    private lateinit var validator: QueueAccessValidator
    private lateinit var entryQueuePort: FakeEntryQueuePort

    @BeforeEach
    fun setUp() {
        entryQueuePort = FakeEntryQueuePort()
        validator =
            QueueAccessValidator(
                entryQueuePort = entryQueuePort
            )
    }

    @Nested
    @DisplayName("대기열 순번 검증 테스트")
    inner class QueueEntryValidationTest {
        @Nested
        @DisplayName("대기열 통과 성공 테스트")
        inner class QueuePassSuccessTest {
            @ValueSource(longs = [1, 10])
            @ParameterizedTest(name = "순번: {0}")
            @DisplayName("대기열 검증 시 진입 가능한 순번인 경우 예외가 발생하면 안된다.")
            fun queuePassTest(sequence: Long) {
                // given
                val queueTokens: MutableMap<Long, QueueToken> =
                    entryQueuePort.saveHundredQueueToken()
                val allowedQueueToken: QueueToken = queueTokens[sequence]!!

                // when & then
                Assertions
                    .assertThatCode {
                        validator.validateQueueToken(
                            allowedQueueToken.userId,
                            allowedQueueToken.queueNumber
                        )
                    }.doesNotThrowAnyException()
            }
        }

        @Nested
        @DisplayName("대기열 통과 실패 테스트")
        inner class QueuePassFailTest {
            @ValueSource(longs = [11])
            @ParameterizedTest(name = "순번: {0}")
            @DisplayName("대기열 검증 시 진입 가능한 순번이 아닌 경우 예외가 발생해야 한다.")
            fun queueFailTest(sequence: Long) {
                // given
                val queueTokens: MutableMap<Long, QueueToken> =
                    entryQueuePort.saveHundredQueueToken()
                val notAllowedQueueToken: QueueToken = queueTokens[sequence]!!

                // when & then
                Assertions
                    .assertThatThrownBy {
                        validator.validateQueueToken(
                            notAllowedQueueToken.userId,
                            notAllowedQueueToken.queueNumber
                        )
                    }.isInstanceOf(CustomException::class.java)
                    .message()
                    .isEqualTo("queue not yet allowed.")
            }

            @Test
            @DisplayName("대기열 검증 시 대기 상태의 토큰이 존재하지 않는 경우 예외가 발생해야 한다.")
            fun queueFailTest2() {
                // given
                val userId: UUID = UUID.randomUUID()
                val completedQueueToken =
                    QueueToken(
                        userId = userId,
                        queueNumber = 1,
                        token = "token",
                        status = QueueToken.Status.COMPLETED
                    )
                entryQueuePort.saveEntryQueueToken(completedQueueToken)

                // when & then
                Assertions
                    .assertThatThrownBy {
                        validator.validateQueueToken(
                            userId = userId,
                            queueNumber = 1
                        )
                    }.isInstanceOf(CustomException::class.java)
                    .message()
                    .isEqualTo("invalid token status.")
            }

            @Test
            @DisplayName("대기열 검증 시 토큰에 있는 순번과 저장되어 있는 순번이 다른 경우 예외가 발생해야 한다.")
            fun queueFailTest3() {
                // given
                val tokenNumber = 1
                val savedNumber = 2
                val userId: UUID = UUID.randomUUID()
                val completedQueueToken =
                    QueueToken(
                        userId = userId,
                        queueNumber = savedNumber,
                        token = "token",
                        status = QueueToken.Status.WAITING
                    )
                entryQueuePort.saveEntryQueueToken(completedQueueToken)

                // when & then
                Assertions
                    .assertThatThrownBy {
                        validator.validateQueueToken(
                            userId = userId,
                            queueNumber = tokenNumber
                        )
                    }.isInstanceOf(CustomException::class.java)
                    .message()
                    .isEqualTo("invalid queue token.")
            }
        }
    }
}
