package kr.hhplus.be.server.pointwallet.application.service

import java.util.UUID
import kr.hhplus.be.server.fake.FakePointWalletPort
import kr.hhplus.be.server.fake.FakeUserPort
import kr.hhplus.be.server.pointwallet.exception.PointWalletException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserPointServiceTest {
    private lateinit var userPointService: UserPointService
    private lateinit var userPort: FakeUserPort
    private lateinit var pointWalletPort: FakePointWalletPort

    @BeforeEach
    fun setUp() {
        userPort = FakeUserPort()
        pointWalletPort = FakePointWalletPort()
        userPointService =
            UserPointService(
                userPort,
                pointWalletPort
            )
    }

    @Nested
    @DisplayName("유저 포인트 서비스 테스트")
    inner class UserPointServiceTest {
        @Nested
        @DisplayName("유저 포인트 충전 테스트")
        inner class ChargeUserPointTest {
            @Nested
            @DisplayName("유저 포인트 충전 성공 테스트")
            inner class ChargeUserPointSuccessTest {
                @Test
                @DisplayName("유저 포인트를 충전 할 수 있어야 한다.")
                fun userPointTest() {
                    // given
                    val userId: UUID = UUID.randomUUID()
                    val amount = 1000L

                    pointWalletPort.saveSingleWallet(userId)

                    // when
                    val actual: Long = userPointService.chargePoint(userId, amount)

                    // then
                    Assertions.assertThat(actual).isGreaterThanOrEqualTo(1000L)
                }
            }

            @Nested
            @DisplayName("유저 포인트 충전 실패 테스트")
            inner class ChargeUserPointFailureTest {
                @Test
                @DisplayName("포인트 지갑이 없는 유저에 대해 포인트 충전 시 예외가 발생 해야한다.")
                fun userPointTest() {
                    // given
                    val userId: UUID = UUID.randomUUID()
                    val amount = 1000L

                    // when & then
                    Assertions
                        .assertThatThrownBy {
                            userPointService.chargePoint(userId, amount)
                        }.isInstanceOf(PointWalletException::class.java)
                        .message()
                        .isEqualTo("not found user point wallet.")
                }
            }
        }

        @Nested
        @DisplayName("유저 포인트 조회 테스트")
        inner class GetUserPointTest {
            @Nested
            @DisplayName("유저 포인트 조회 성공 테스트")
            inner class GetUserPointSuccessTest {
                @Test
                @DisplayName("유저 포인트를 조회 할 수 있어야 한다.")
                fun userPointTest() {
                    // given
                    val userId: UUID = UUID.randomUUID()
                    val point: Long = 100L

                    pointWalletPort.saveSingleWallet(userId, point)

                    // when
                    val actual: Long = userPointService.getPoint(userId)

                    // then
                    Assertions.assertThat(actual).isEqualTo(100L)
                }
            }

            @Nested
            @DisplayName("유저 포인트 조회 실패 테스트")
            inner class GetUserPointFailureTest {
                @Test
                @DisplayName("포인트 지갑이 없는 유저에 대해 포인트 조회 시 예외가 발생 해야한다.")
                fun userPointTest() {
                    // given
                    val userId: UUID = UUID.randomUUID()

                    // when & then
                    Assertions
                        .assertThatThrownBy {
                            userPointService.getPoint(userId)
                        }.isInstanceOf(PointWalletException::class.java)
                        .message()
                        .isEqualTo("not found user point wallet.")
                }
            }
        }
    }
}
