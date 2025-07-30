package kr.hhplus.be.server.steps

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import java.time.LocalDate
import kr.hhplus.be.server.reservation.adapter.web.request.MakeReservationRequest
import org.hamcrest.Matchers
import org.springframework.http.MediaType

object ReservationSteps {
    fun makeReservation(
        concertId: Long,
        token: String
    ) {
        val date: String = ConcertSteps.getAvailableDates(concertId, token).first()
        val seat: Int = ConcertSteps.getAvailableSeats(concertId, token, date)
        val request =
            MakeReservationRequest(
                date = LocalDate.parse(date),
                seat = seat.toLong()
            )
        Given {
            header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            header("EntryQueueToken", token)
            body(request)
        } When {
            post("/api/reservations")
        } Then {
            body("code", Matchers.equalTo(200))
        }
    }
}
