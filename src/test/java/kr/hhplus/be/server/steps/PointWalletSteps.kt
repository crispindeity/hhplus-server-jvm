package kr.hhplus.be.server.steps

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import kr.hhplus.be.server.pointwallet.adapter.web.request.ChargePointsRequest
import org.hamcrest.Matchers
import org.springframework.http.MediaType

object PointWalletSteps {
    fun chargePoint(
        userId: String,
        amount: Long,
        token: String
    ) {
        val request = ChargePointsRequest(amount)
        Given {
            header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            header("EntryQueueToken", token)
            body(request)
            pathParam("id", userId)
        } When {
            post("/api/users/{id}/points/charge")
        } Then {
            body("code", Matchers.equalTo(200))
        }
    }
}
