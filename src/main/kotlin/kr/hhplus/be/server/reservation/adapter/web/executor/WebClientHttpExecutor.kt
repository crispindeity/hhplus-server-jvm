package kr.hhplus.be.server.reservation.adapter.web.executor

import kr.hhplus.be.server.reservation.application.service.dto.MakeReservationEvent
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
internal class WebClientHttpExecutor(
    private val webClient: WebClient
) : HttpExecutor {
    override fun sendReservationInfo(request: MakeReservationEvent): HttpStatusCode? =
        webClient
            .post()
            .uri("/post")
            .bodyValue(BodyInserters.fromValue(request))
            .exchangeToMono { response ->
                Mono.just(response.statusCode())
            }.block()
}
