package kr.hhplus.be.server.docs

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.concertschedule.application.port.ConcertSchedulePort
import kr.hhplus.be.server.concertschedule.domain.ConcertSchedule
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.concertseat.domain.ConcertSeat
import kr.hhplus.be.server.config.TestConfig
import kr.hhplus.be.server.payment.application.port.PaymentPort
import kr.hhplus.be.server.payment.domain.Payment
import kr.hhplus.be.server.reservation.adapter.web.ReservationController
import kr.hhplus.be.server.reservation.adapter.web.request.MakeReservationRequest
import kr.hhplus.be.server.reservation.application.port.ReservationPort
import kr.hhplus.be.server.reservation.domain.Reservation
import kr.hhplus.be.server.seat.application.port.SeatPort
import kr.hhplus.be.server.seat.domain.Seat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
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
@Import(TestConfig::class)
@WebMvcTest(controllers = [ReservationController::class])
class ReservationControllerDocsTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private val objectMapper = ObjectMapper()

    @Autowired
    private lateinit var concertSchedulePort: ConcertSchedulePort

    @Autowired
    private lateinit var concertSeatPort: ConcertSeatPort

    @Autowired
    private lateinit var reservationPort: ReservationPort

    @Autowired
    private lateinit var paymentPort: PaymentPort

    @Autowired
    private lateinit var seatPort: SeatPort

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
    @DisplayName("[문서] 예약 생성 요청")
    fun makeReservation() {
        // given
        val request =
            MakeReservationRequest(
                date = LocalDate.now().plusDays(1),
                seat = 1L
            )
        val concertSeatId = 1L
        val scheduleId = 10L
        val seatId = 100L
        val concertId = 1000L
        val date: LocalDate = LocalDate.now().plusDays(1)
        val userUUID: UUID = UUID.randomUUID()

        val concertSeat =
            ConcertSeat(
                id = concertSeatId,
                scheduleId = scheduleId,
                seatId = seatId,
                status = ConcertSeat.SeatStatus.AVAILABLE
            )

        val schedule =
            ConcertSchedule(
                id = scheduleId,
                concertId = concertId,
                date = date
            )

        val reservedAt: LocalDateTime = LocalDateTime.now()
        val expiresAt: LocalDateTime = reservedAt.plusMinutes(15)

        val reservation =
            Reservation(
                userId = userUUID,
                concertId = concertId,
                concertSeatId = concertSeatId,
                status = Reservation.Status.IN_PROGRESS,
                reservedAt = reservedAt,
                expiresAt = expiresAt
            )

        val seat =
            Seat(
                id = seatId,
                number = 1L,
                price = 1000L
            )

        val payment =
            Payment(
                id = 1L,
                userId = userUUID,
                price = seat.price
            )

        // mock
        BDDMockito.given(concertSeatPort.getConcertSeat(concertSeatId)).willReturn(concertSeat)
        BDDMockito.given(concertSchedulePort.getSchedule(scheduleId)).willReturn(schedule)
        BDDMockito.given(seatPort.getSeat(seatId)).willReturn(seat)
        BDDMockito.willDoNothing().given(reservationPort).save(reservation)
        BDDMockito.given(paymentPort.save(payment)).willReturn(payment.id)

        // when
        val result: ResultActions =
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/api/reservations")
                        .content(objectMapper.writeValueAsString(request))
                        .requestAttr("userId", userUUID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
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
                    identifier = "예약 생성",
                    resourceDetails =
                        ResourceSnippetParameters
                            .builder()
                            .tag("예약")
                            .summary("예약 생성 요청 API")
                            .description("좌석을 예약할 때 사용되는 API 입니다."),
                    snippets =
                        arrayOf(
                            PayloadDocumentation.requestFields(
                                PayloadDocumentation
                                    .fieldWithPath("date")
                                    .type(JsonFieldType.STRING)
                                    .description("예약 일자"),
                                PayloadDocumentation
                                    .fieldWithPath("seat")
                                    .type(JsonFieldType.NUMBER)
                                    .description("좌석")
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
                            ),
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
                                    .fieldWithPath("result.userId")
                                    .type(JsonFieldType.STRING)
                                    .description("유저 식별자"),
                                PayloadDocumentation
                                    .fieldWithPath("result.concertSeatId")
                                    .type(JsonFieldType.NUMBER)
                                    .description("콘서트 좌석 식별자"),
                                PayloadDocumentation
                                    .fieldWithPath("result.concertDate")
                                    .type(JsonFieldType.STRING)
                                    .description("콘서트 날짜"),
                                PayloadDocumentation
                                    .fieldWithPath("result.reservedAt")
                                    .type(JsonFieldType.STRING)
                                    .description("예약 일자"),
                                PayloadDocumentation
                                    .fieldWithPath("result.expiresAt")
                                    .type(JsonFieldType.STRING)
                                    .description("예약 만료 일자")
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

    @Test
    @DisplayName("[문서] 예약 생성 요청 - 잘못된 요청")
    fun makeReservationBadRequest() {
        // given
        val request =
            MakeReservationRequest(
                date = LocalDate.now().plusDays(1),
                seat = -1L
            )

        // when
        val result: ResultActions =
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/api/reservations")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("EntryQueueToken", "token")
                )

        // then
        result
            .andExpectAll(
                MockMvcResultMatchers.status().isOk,
                MockMvcResultMatchers
                    .jsonPath("$.code")
                    .value(ErrorCode.INVALID_REQUEST_VALUE.code),
                MockMvcResultMatchers
                    .jsonPath("$.message")
                    .value(ErrorCode.INVALID_REQUEST_VALUE.message)
            ).andDo(
                MockMvcRestDocumentationWrapper.document(
                    identifier = "예약 생성 - 잘못된 요청",
                    resourceDetails =
                        ResourceSnippetParameters
                            .builder()
                            .tag("예약"),
                    snippets =
                        arrayOf(
                            PayloadDocumentation.requestFields(
                                PayloadDocumentation
                                    .fieldWithPath("date")
                                    .type(JsonFieldType.STRING)
                                    .description("예약 일자"),
                                PayloadDocumentation
                                    .fieldWithPath("seat")
                                    .type(JsonFieldType.NUMBER)
                                    .description("좌석")
                            ),
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
                                    .fieldWithPath("result.errors[0].field")
                                    .type(JsonFieldType.STRING)
                                    .description("잘못 요청한 필드 이름"),
                                PayloadDocumentation
                                    .fieldWithPath("result.errors[0].value")
                                    .type(JsonFieldType.NUMBER)
                                    .description("잘못 요청한 필드 값")
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
