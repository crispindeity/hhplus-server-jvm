package kr.hhplus.be.server.reservation.application.event

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.util.UUID
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concertseat.application.event.ConcertSeatHoldFailedEvent
import kr.hhplus.be.server.fake.FakeReservationEventPort
import kr.hhplus.be.server.fake.FakeReservationPort
import kr.hhplus.be.server.fake.FakeReservationWebPort
import kr.hhplus.be.server.fake.FakeRunner
import kr.hhplus.be.server.fixture.ReservationFixture
import kr.hhplus.be.server.reservation.domain.Reservation
import kr.hhplus.be.server.testutil.attachListAppenderFor
import org.springframework.http.HttpStatusCode

class ReservationEventPublisherTest :
    BehaviorSpec({
        lateinit var webPort: FakeReservationWebPort
        lateinit var reservationPort: FakeReservationPort
        lateinit var reservationEventPublisher: ReservationEventPublisher

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
            reservationEventPublisher =
                ReservationEventPublisher(
                    reservationPort = reservationPort,
                    reservationWebPort = webPort,
                    transactional = Transactional(FakeRunner()),
                    reservationEventTracePort = FakeReservationEventPort()
                )
        }

        context("예약 생성 이벤트") {
            given("예약 생성 이벤트 발생하고") {
                `when`("예약 정보 데이터 전송 시") {
                    then("정상적으로 전송 돼야한다.") {
                        val event =
                            MakeReservationEvent(
                                eventId = UUID.randomUUID(),
                                reservationId = 1L,
                                userId = UUID.randomUUID(),
                                seatId = 1L,
                                concertSeatId = 1L,
                                scheduleId = 1L,
                                reservedAt = LocalDateTime.now()
                            )

                        shouldNotThrowAny {
                            reservationEventPublisher.handleMakeReservationEvent(event)
                        }
                        webPort.callCount shouldBe 1
                        webPort.lastEvent?.reservationId shouldBe 1L
                    }

                    `when`("예약 정보 데이터 전송 응답 코드가 200이 아닌 경우") {
                        then("예외는 던지지 않지만 실패 로그가 남아야 한다.") {
                            val (_, appender: ListAppender<ILoggingEvent>) =
                                attachListAppenderFor(
                                    ReservationEventPublisher::class.java,
                                    Level.WARN
                                )
                            val event =
                                MakeReservationEvent(
                                    eventId = UUID.randomUUID(),
                                    reservationId = 0L,
                                    userId = UUID.randomUUID(),
                                    seatId = 1L,
                                    concertSeatId = 1L,
                                    scheduleId = 1L,
                                    reservedAt = LocalDateTime.now()
                                )

                            shouldNotThrowAny {
                                reservationEventPublisher.handleMakeReservationEvent(event)
                            }

                            webPort.callCount shouldBe 1
                            webPort.lastEvent?.reservationId shouldBe 0L
                            appender.list.any { it.level == Level.WARN } shouldBe true
                        }
                    }
                }
            }
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

                        reservationEventPublisher.handleConcertSeatHoldFailedEvent(event)

                        val reservation: Reservation = reservationPort.getReservation(1L)!!

                        reservation.status shouldBe Reservation.Status.ERROR
                    }
                }
            }
        }
    })
