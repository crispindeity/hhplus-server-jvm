package kr.hhplus.be.server.docs

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceSnippetParameters
import java.util.UUID
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.concertseat.domain.ConcertSeat
import kr.hhplus.be.server.config.ConcertTestConfig
import kr.hhplus.be.server.config.EntryQueueTestConfig
import kr.hhplus.be.server.config.PaymentTestConfig
import kr.hhplus.be.server.config.PointTestConfig
import kr.hhplus.be.server.config.ReservationTestConfig
import kr.hhplus.be.server.config.SeatTestConfig
import kr.hhplus.be.server.config.TransactionalTestConfig
import kr.hhplus.be.server.payment.adapter.web.PaymentController
import kr.hhplus.be.server.payment.application.port.PaymentPort
import kr.hhplus.be.server.payment.domain.Payment
import kr.hhplus.be.server.pointwallet.application.port.PointWalletPort
import kr.hhplus.be.server.pointwallet.domain.PointWallet
import kr.hhplus.be.server.queuetoken.application.port.EntryQueuePort
import kr.hhplus.be.server.queuetoken.domain.QueueToken
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.reservation.domain.Reservation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.snippet.Attributes
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@ControllerDocsTest
@Import(
    PaymentTestConfig::class,
    TransactionalTestConfig::class,
    ReservationTestConfig::class,
    PointTestConfig::class,
    ConcertTestConfig::class,
    EntryQueueTestConfig::class,
    SeatTestConfig::class
)
@WebMvcTest(controllers = [PaymentController::class])
class PaymentControllerDocsTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var reservationPort: ReservationPort

    @Autowired
    private lateinit var paymentPort: PaymentPort

    @Autowired
    private lateinit var pointWalletPort: PointWalletPort

    @Autowired
    private lateinit var concertSeatPort: ConcertSeatPort

    @Autowired
    private lateinit var entryQueuePort: EntryQueuePort

    @BeforeEach
    fun setUp(
        context: WebApplicationContext,
        restDocumentation: RestDocumentationContextProvider
    ) {
        mockMvc =
            MockMvcBuilders
                .webAppContextSetup(context)
                .apply<DefaultMockMvcBuilder>(
                    MockMvcRestDocumentation.documentationConfiguration(
                        restDocumentation
                    )
                ).build()
    }

    @Test
    @DisplayName("[문서] 포인트 결제 요청")
    fun paymentPoints() {
        // given
        val userId: UUID = UUID.randomUUID()
        val reservation =
            Reservation(
                id = 1L,
                userId = userId,
                concertSeatId = 1L,
                concertId = 1L,
                paymentId = 1L,
                status = Reservation.Status.IN_PROGRESS
            )
        val payment =
            Payment(
                id = 1L,
                userId = userId,
                price = 1000L
            )

        // mock

        BDDMockito.given(reservationPort.getAll(userId.toString())).willReturn(
            listOf(
                reservation
            )
        )
        BDDMockito.given(paymentPort.getAll(anyList())).willReturn(listOf(payment))
        BDDMockito.given(pointWalletPort.getWallet(userId)).willReturn(
            PointWallet(
                id = 1L,
                userId = userId,
                balance = 2000L
            )
        )
        BDDMockito.given(concertSeatPort.getConcertSeat(1L)).willReturn(
            ConcertSeat(
                id = 1L,
                scheduleId = 1L,
                seatId = 1L,
                status = ConcertSeat.SeatStatus.HELD
            )
        )
        BDDMockito.given(entryQueuePort.getEntryQueueToken(userId)).willReturn(
            QueueToken(
                id = 1L,
                userId = userId,
                queueNumber = 1,
                token = "token"
            )
        )

        // when
        val result: ResultActions =
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/api/payments")
                        .requestAttr("userId", userId.toString())
                        .header("EntryQueueToken", "token")
                )

        // then
        result
            .andExpectAll(
                MockMvcResultMatchers.status().isOk,
                MockMvcResultMatchers.jsonPath("$.code").value(200),
                MockMvcResultMatchers.jsonPath("$.message").value("success")
            ).andDo(
                MockMvcRestDocumentationWrapper.document(
                    identifier = "포인트 결제",
                    resourceDetails =
                        ResourceSnippetParameters
                            .builder()
                            .tag("결제")
                            .summary("포인트 결제 요청 API")
                            .description("포인트 결제 시 사용되는 API"),
                    snippets =
                        arrayOf(
                            PayloadDocumentation.responseFields(
                                PayloadDocumentation
                                    .fieldWithPath("code")
                                    .type(JsonFieldType.NUMBER)
                                    .description("응답 코드"),
                                PayloadDocumentation
                                    .fieldWithPath("message")
                                    .type(JsonFieldType.STRING)
                                    .description("응답 메시지"),
                                PayloadDocumentation
                                    .fieldWithPath("result.totalPrice")
                                    .type(JsonFieldType.NUMBER)
                                    .description("가격"),
                                PayloadDocumentation
                                    .fieldWithPath("result.reservationCount")
                                    .type(JsonFieldType.NUMBER)
                                    .description("예약 건수")
                            ),
                            HeaderDocumentation.requestHeaders(
                                HeaderDocumentation
                                    .headerWithName("EntryQueueToken")
                                    .description("대기열 토큰")
                                    .attributes(
                                        Attributes
                                            .key("EntryQueueToken")
                                            .value("Token")
                                    )
                            )
                        ),
                    requestPreprocessor =
                        Preprocessors.preprocessRequest(
                            Preprocessors.prettyPrint()
                        ),
                    responsePreprocessor =
                        Preprocessors.preprocessResponse(
                            Preprocessors.prettyPrint()
                        )
                )
            )
    }
}
