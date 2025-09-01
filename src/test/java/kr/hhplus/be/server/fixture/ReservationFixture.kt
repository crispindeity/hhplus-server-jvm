package kr.hhplus.be.server.fixture

import java.util.UUID
import kr.hhplus.be.server.reservation.domain.Reservation

internal object ReservationFixture {
    fun makeReservation(): Reservation =
        Reservation(
            id = 1L,
            userId = UUID.fromString(UserFixture.getUserId()),
            concertId = 1L,
            concertSeatId = 1L,
            status = Reservation.Status.INIT
        )
}
