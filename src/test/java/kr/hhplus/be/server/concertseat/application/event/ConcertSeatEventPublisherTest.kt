package kr.hhplus.be.server.concertseat.application.event

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.util.UUID
import kr.hhplus.be.server.common.transactional.AfterCommitExecutor
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concertseat.domain.ConcertSeat
import kr.hhplus.be.server.fake.FakeApplicationEventPublisher
import kr.hhplus.be.server.fake.FakeConcertSeatPort
import kr.hhplus.be.server.fake.FakeRunner
import kr.hhplus.be.server.fake.FakeSeatPort
import kr.hhplus.be.server.fixture.UserFixture
import kr.hhplus.be.server.reservation.application.event.MakeReservationEvent

class ConcertSeatEventPublisherTest :
    BehaviorSpec({
        lateinit var concertSeatEventPublisher: ConcertSeatEventPublisher
        lateinit var concertSeatPort: FakeConcertSeatPort

        beforeTest {
            concertSeatPort = FakeConcertSeatPort(FakeSeatPort())
            concertSeatEventPublisher =
                ConcertSeatEventPublisher(
                    concertSeatPort = concertSeatPort,
                    transactional = Transactional(FakeRunner()),
                    afterCommitExecutor = AfterCommitExecutor(),
                    eventPublisher = FakeApplicationEventPublisher()
                )
        }

        context("좌석 점유 이벤트") {
            given("좌석 점유 이벤트가 발생하고") {
                `when`("좌석 점유 시") {
                    then("정상적으로 점유 돼야한다.") {
                        concertSeatPort.saveSingleSeat(1L)

                        concertSeatEventPublisher.handleMakeReservationEvent(
                            MakeReservationEvent(
                                eventId = UUID.randomUUID(),
                                userId = UUID.fromString(UserFixture.getUserId()),
                                reservationId = 1L,
                                seatId = 1L,
                                concertSeatId = 1L,
                                scheduleId = 1L,
                                reservedAt = LocalDateTime.now()
                            )
                        )

                        val concertSeat: ConcertSeat = concertSeatPort.getConcertSeat(1L)!!

                        concertSeat.status shouldBe ConcertSeat.SeatStatus.HELD
                    }
                }
            }
        }
    })
