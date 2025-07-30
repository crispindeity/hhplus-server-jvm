package kr.hhplus.be.server.steps

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import kr.hhplus.be.server.queuetoken.adapter.web.request.EntryQueueTokenRequest
import org.hamcrest.Matchers
import org.springframework.http.MediaType

internal object EntryQueueTokenSteps {
    fun getEntryQueueToken(userId: String): String {
        val request = EntryQueueTokenRequest(userId)
        val response: Response =
            Given {
                contentType(MediaType.APPLICATION_JSON_VALUE)
                body(request)
            } When {
                post("/api/queue/entry-token")
            } Then {
                body("code", Matchers.equalTo(200))
            } Extract {
                response()
            }
        return response.headers.getValue("EntryQueueToken")
    }
}
