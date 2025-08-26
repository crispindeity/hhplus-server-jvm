package kr.hhplus.be.server.reservation.application.service

import java.time.LocalDate
import java.util.UUID
import kr.hhplus.be.server.common.lock.RedisLocks
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.concertschedule.application.port.ConcertSchedulePort
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.concertseat.domain.ConcertSeat
import kr.hhplus.be.server.payment.application.port.PaymentPort
import kr.hhplus.be.server.payment.domain.Payment
import kr.hhplus.be.server.ranking.application.port.RankingPort
import kr.hhplus.be.server.reservation.adapter.web.response.MakeReservationResponse
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.reservation.domain.Reservation
import kr.hhplus.be.server.seathold.application.port.SeatHoldPort
import kr.hhplus.be.server.seathold.domain.SeatHold
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
internal class ReservationService(
    private val seatHoldPort: SeatHoldPort,
    private val concertSeatPort: ConcertSeatPort,
    private val reservationPort: ReservationPort,
    private val paymentPort: PaymentPort,
    private val reservationContextLoader: ReservationContextLoader,
    private val transactional: Transactional,
    private val rankingPort: RankingPort,
    private val concertSchedulePort: ConcertSchedulePort
) {
    private val logger: Logger = Log.getLogger(ReservationService::class.java)

    @RedisLocks(
        keys = ["'concertSeat:' + #concertSeatId"],
        waitSeconds = 0,
        leaseSeconds = 3
    )
    fun makeReservation(
        date: LocalDate,
        concertSeatId: Long,
        userId: String
    ): MakeReservationResponse =
        Log.logging(logger) { log ->
            log["method"] = "makeReservation()"

            val userUUID: UUID = UUID.fromString(userId)
            val context: ReservationContext =
                reservationContextLoader.load(concertSeatId, date)

            val response: MakeReservationResponse =
                transactional.run {
                    val heldSeat: ConcertSeat = context.concertSeat.held()
                    concertSeatPort.update(heldSeat)

                    seatHoldPort.save(SeatHold(concertSeatId = concertSeatId, userId = userUUID))

                    val paymentId: Long =
                        paymentPort.save(Payment(userId = userUUID, price = context.seat.price))

                    val reservation =
                        Reservation(
                            userId = userUUID,
                            concertSeatId = concertSeatId,
                            concertId = context.schedule.concertId,
                            paymentId = paymentId,
                            status = Reservation.Status.IN_PROGRESS
                        )
                    reservationPort.save(reservation)

                    MakeReservationResponse(
                        userId = userId,
                        concertSeatId = context.concertSeat.id,
                        reservedAt = reservation.reservedAt,
                        expiresAt = reservation.expiresAt,
                        concertDate = context.schedule.date
                    )
                }

            rankingPort.saveFirstReservationTime(
                concertId = context.schedule.concertId,
                concertDate = context.schedule.date,
                reservationAt = response.reservedAt
            )

            val remainingConcertSeat: Long =
                concertSchedulePort.decreaseSeatCount(
                    concertId = context.schedule.concertId,
                    scheduleId = context.schedule.id
                )

            if (remainingConcertSeat == 0L) {
                rankingPort.saveSoldOutTime(
                    concertId = context.schedule.concertId,
                    scheduleId = context.schedule.id,
                    concertDate = context.schedule.date,
                    soldOutAt = response.reservedAt
                )
                rankingPort.saveSoldOutRanking(
                    concertId = context.schedule.concertId,
                    scheduleId = context.schedule.id,
                    concertDate = context.schedule.date
                )
            }

            return@logging response
        }
}
