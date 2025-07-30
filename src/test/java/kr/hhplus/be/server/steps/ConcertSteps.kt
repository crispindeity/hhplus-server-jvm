package kr.hhplus.be.server.steps

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.hamcrest.Matchers
import org.springframework.http.MediaType

internal object ConcertSteps {
    fun getAvailableDates(
        concertId: Long,
        token: String
    ): List<String> {
        val response: Response =
            Given {
                header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                header("EntryQueueToken", token)
                pathParam("id", concertId)
            } When {
                get("/api/concerts/{id}/reservations/available-dates")
            } Then {
                body("code", Matchers.equalTo(200))
            } Extract {
                response()
            }
        return response.jsonPath().getList("result.dates")
    }

    fun getAvailableSeats(
        concertId: Long,
        token: String,
        date: String
    ): Int {
        val response: Response =
            Given {
                header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                header("EntryQueueToken", token)
                pathParam("id", concertId)
                param("date", date)
            } When {
                get("/api/concerts/{id}/reservations/available-seats")
            } Then {
                body("code", Matchers.equalTo(200))
            } Extract {
                response()
            }
        return response.jsonPath().getInt("result.seats[0].id")
    }
}
