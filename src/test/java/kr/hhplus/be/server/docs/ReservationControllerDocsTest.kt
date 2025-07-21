package kr.hhplus.be.server.docs

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDate
import java.time.LocalTime
import kr.hhplus.be.server.adapter.web.dto.request.MakeReservationRequest
import kr.hhplus.be.server.common.exception.ErrorCode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
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

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension::class)
class ReservationControllerDocsTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private val objectMapper = ObjectMapper()

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
                time = LocalTime.now(),
                seat = 1L
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
                                    .fieldWithPath("time")
                                    .type(JsonFieldType.STRING)
                                    .description("예약 시간"),
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
                                    .fieldWithPath("result.id")
                                    .type(JsonFieldType.NUMBER)
                                    .description("예약 식별자"),
                                PayloadDocumentation
                                    .fieldWithPath("result.userId")
                                    .type(JsonFieldType.NUMBER)
                                    .description("유저 식별자"),
                                PayloadDocumentation
                                    .fieldWithPath("result.concertId")
                                    .type(JsonFieldType.NUMBER)
                                    .description("콘서트 식별자"),
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
                time = LocalTime.now(),
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
                                    .fieldWithPath("time")
                                    .type(JsonFieldType.STRING)
                                    .description("예약 시간"),
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
