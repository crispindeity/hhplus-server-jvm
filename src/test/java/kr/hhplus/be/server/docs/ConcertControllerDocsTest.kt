package kr.hhplus.be.server.docs

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceSnippetParameters
import java.time.LocalDate
import kr.hhplus.be.server.concert.adapter.web.ConcertController
import kr.hhplus.be.server.concert.application.port.ConcertPort
import kr.hhplus.be.server.concert.application.service.dto.AvailableSeatDto
import kr.hhplus.be.server.concertschedule.application.port.ConcertSchedulePort
import kr.hhplus.be.server.concertschedule.domain.ConcertSchedule
import kr.hhplus.be.server.concertseat.application.port.ConcertSeatPort
import kr.hhplus.be.server.concertseat.domain.ConcertSeat
import kr.hhplus.be.server.config.TestConfig
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
import org.springframework.restdocs.request.RequestDocumentation
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
@WebMvcTest(controllers = [ConcertController::class])
class ConcertControllerDocsTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var concertPort: ConcertPort

    @Autowired
    private lateinit var concertSchedulePort: ConcertSchedulePort

    @Autowired
    private lateinit var concertSeatPort: ConcertSeatPort

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
    @DisplayName("[문서] 콘서트 예약 가능 날짜 조회 요청")
    fun findAvailableDates() {
        // given
        val concertId = 1L
        val schedules: List<ConcertSchedule> =
            listOf(
                ConcertSchedule(id = 1L, concertId = concertId, date = LocalDate.now()),
                ConcertSchedule(id = 2L, concertId = concertId, date = LocalDate.now())
            )

        // mock
        BDDMockito.given(concertPort.existsConcert(concertId)).willReturn(true)
        BDDMockito.given(concertSchedulePort.getAvailableSchedules(concertId)).willReturn(schedules)

        // when
        val result: ResultActions =
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .get("/api/concerts/{id}/reservations/available-dates", concertId)
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
                    identifier = "콘서트 예약 가능 날짜 조회",
                    resourceDetails =
                        ResourceSnippetParameters
                            .builder()
                            .tag("콘서트")
                            .summary("콘서트 예약 가능 날짜 조회 API")
                            .description("콘서트 예약 가능 날짜를 조회할 때 사용하는 API"),
                    snippets =
                        arrayOf(
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
                                    .fieldWithPath("result.dates")
                                    .type(JsonFieldType.ARRAY)
                                    .description("예약 가능 날짜 리스트")
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
    @DisplayName("[문서] 콘서트 예약 가능 좌석 조회 요청")
    fun findAvailableSeats() {
        // given
        val concertId = 1L
        val param: LocalDate = LocalDate.now().plusDays(1)
        val scheduleId = 10L
        val schedule = ConcertSchedule(id = scheduleId, concertId = concertId, date = param)
        val availableSeats: List<AvailableSeatDto> =
            listOf(
                AvailableSeatDto(
                    id = 1,
                    number = 1,
                    price = 1000,
                    status = ConcertSeat.SeatStatus.AVAILABLE
                ),
                AvailableSeatDto(
                    id = 2,
                    number = 2,
                    price = 3000,
                    status = ConcertSeat.SeatStatus.AVAILABLE
                )
            )

        // mock
        BDDMockito
            .given(concertPort.existsConcert(concertId))
            .willReturn(true)
        BDDMockito
            .given(concertSchedulePort.getSchedule(concertId, date = param))
            .willReturn(schedule)
        BDDMockito
            .given(concertSeatPort.getAvailableSeats(scheduleId))
            .willReturn(availableSeats)

        // when
        val result: ResultActions =
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .get("/api/concerts/{id}/reservations/available-seats", concertId)
                        .param("date", param.toString())
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
                    identifier = "콘서트 예약 가능 좌석 조회",
                    resourceDetails =
                        ResourceSnippetParameters
                            .builder()
                            .tag("콘서트")
                            .summary("콘서트 예약 가능 좌석 조회 API")
                            .description("콘서트 예약 가능 좌석을 조회할 때 사용하는 API"),
                    snippets =
                        arrayOf(
                            RequestDocumentation.queryParameters(
                                RequestDocumentation
                                    .parameterWithName("date")
                                    .description("좌석 조회 날짜")
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
                                    .fieldWithPath("result.seats")
                                    .type(JsonFieldType.ARRAY)
                                    .description("예약 가능 좌석 리스트"),
                                PayloadDocumentation
                                    .fieldWithPath("result.seats[].id")
                                    .type(JsonFieldType.NUMBER)
                                    .description("좌석 식별자"),
                                PayloadDocumentation
                                    .fieldWithPath("result.seats[].number")
                                    .type(JsonFieldType.NUMBER)
                                    .description("좌석 번호"),
                                PayloadDocumentation
                                    .fieldWithPath("result.seats[].price")
                                    .type(JsonFieldType.NUMBER)
                                    .description("좌석 가격")
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
