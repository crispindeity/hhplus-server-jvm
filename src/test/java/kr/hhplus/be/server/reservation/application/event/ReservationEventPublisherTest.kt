package kr.hhplus.be.server.reservation.application.event

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.util.UUID
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concertseat.application.event.ConcertSeatHoldFailedEvent
import kr.hhplus.be.server.fake.FakeReservationEventTracePort
import kr.hhplus.be.server.fake.FakeReservationPort
import kr.hhplus.be.server.fake.FakeReservationWebPort
import kr.hhplus.be.server.fake.FakeRunner
import kr.hhplus.be.server.fixture.ReservationFixture
import kr.hhplus.be.server.reservation.domain.Reservation
import org.springframework.http.HttpStatusCode

class ReservationEventPublisherTest :
    BehaviorSpec({
        lateinit var webPort: FakeReservationWebPort
        lateinit var reservationPort: FakeReservationPort
        lateinit var reservationEventReader: ReservationEventReader

        beforeTest {
            webPort =
                FakeReservationWebPort { event ->
                    if (event.reservationId == 0L) {
                        HttpStatusCode.valueOf(500)
                    } else {
                        HttpStatusCode.valueOf(200)
                    }
                }
            reservationPort = FakeReservationPort()
            reservationEventReader =
                ReservationEventReader(
                    reservationPort = reservationPort,
                    transactional = Transactional(FakeRunner()),
                    reservationEventTracePort = FakeReservationEventTracePort()
                )
        }

        context("좌석 점유 실패 이벤트") {
            given("예약 생성 후") {
                `when`("좌석 점유 실패 이벤트 발생 시") {
                    then("생성된 예약의 상태가 INIT에서 ERROR로 변경 돼야한다.") {
                        reservationPort.save(ReservationFixture.makeReservation())
                        val event =
                            ConcertSeatHoldFailedEvent(
                                eventId = UUID.randomUUID(),
                                reservationId = 1L,
                                concertSeatId = 1L
                            )

                        reservationEventReader.handleConcertSeatHoldFailedEvent(event)

                        val reservation: Reservation = reservationPort.getReservation(1L)!!

                        reservation.status shouldBe Reservation.Status.ERROR
                    }
                }
            }
        }
    })
