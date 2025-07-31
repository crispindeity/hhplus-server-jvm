package kr.hhplus.be.server.scheduler.integration

import java.time.LocalDateTime
import kr.hhplus.be.server.common.integration.AbstractIntegrationTest
import kr.hhplus.be.server.fixture.UserFixture
import kr.hhplus.be.server.scheduler.orchestrator.ReservationOrchestrator
import kr.hhplus.be.server.steps.EntryQueueTokenSteps
import kr.hhplus.be.server.steps.ReservationSteps
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ReservationOrchestratorIntegrationTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var reservationOrchestrator: ReservationOrchestrator

    @Nested
    @DisplayName("예약 오케스트레이터 통합 테스트")
    inner class ReservationOrchestratorIntegrationTest {
        @Nested
        @DisplayName("예약 만료 처리 기능 테스트")
        inner class ExpireReservationTest {
            @Nested
            @DisplayName("예약 만료 처리 성공 테스트")
            inner class ExpireReservationSuccessTest {
                @Test
                @DisplayName("예약을 만료 처리 할 수 있어야 한다.")
                fun expireTest() {
                    // given
                    val concertId = 1L
                    val userId: String = UserFixture.getUserId()
                    val token: String = EntryQueueTokenSteps.getEntryQueueToken(userId)
                    val currentDateTime: LocalDateTime = LocalDateTime.now().plusMinutes(5)
                    ReservationSteps.makeReservation(concertId, token)

                    // when & then
                    Assertions
                        .assertThatCode {
                            reservationOrchestrator.expireReservations(currentDateTime)
                        }.doesNotThrowAnyException()
                }
            }
        }
    }
}
