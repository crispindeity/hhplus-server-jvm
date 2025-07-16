package kr.hhplus.be.server.docs

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.databind.ObjectMapper
import kr.hhplus.be.server.adapter.web.dto.request.FindAvailableDatesRequest
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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
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
    @DisplayName("[문서] 예약 가능 날짜 조회 요청")
    fun findAvailableDates() {
        // given
        val request = FindAvailableDatesRequest(1L)

        // when
        val result: ResultActions =
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .get("/api/reservations/available-dates")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )

        // then
        result
            .andExpectAll(
                MockMvcResultMatchers.status().isOk,
                MockMvcResultMatchers.jsonPath("$.code").value(200),
                MockMvcResultMatchers.jsonPath("$.message").value("success")
            ).andDo(
                MockMvcRestDocumentationWrapper.document(
                    identifier = "예약 가능 날짜 조회",
                    resourceDetails =
                        ResourceSnippetParameters
                            .builder()
                            .tag("예약")
                            .summary("예약 가능 날짜 조회 API")
                            .description("예약 가능 날짜를 조회할 때 사용하는 API"),
                    snippets =
                        arrayOf(
                            PayloadDocumentation.requestFields(
                                PayloadDocumentation
                                    .fieldWithPath("concertId")
                                    .type(JsonFieldType.NUMBER)
                                    .description("콘서트 식별 아이디")
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
    @DisplayName("[문서] 예약 가능 날짜 조회 요청 - 잘못된 요청")
    fun findAvailableDatesBadRequest() {
        // given
        val request = FindAvailableDatesRequest(-1L)

        // when
        val result: ResultActions =
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .get("/api/reservations/available-dates")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )

        // then
        result
            .andExpectAll(
                MockMvcResultMatchers.status().isBadRequest,
                MockMvcResultMatchers.jsonPath("$.code").value(400),
                MockMvcResultMatchers.jsonPath("$.message").value("bad request")
            ).andDo(
                MockMvcRestDocumentationWrapper.document(
                    identifier = "예약 가능 날짜 조회 - 잘못된 요청",
                    resourceDetails =
                        ResourceSnippetParameters
                            .builder()
                            .tag("예약")
                            .summary("예약 가능 날짜 조회 요청 실패")
                            .description("잘못된 값으로 조회 요청 시 요청에 실패"),
                    snippets =
                        arrayOf(
                            PayloadDocumentation.requestFields(
                                PayloadDocumentation
                                    .fieldWithPath("concertId")
                                    .type(JsonFieldType.NUMBER)
                                    .description("콘서트 식별 아이디")
                            ),
                            PayloadDocumentation.responseFields(
                                PayloadDocumentation
                                    .fieldWithPath("code")
                                    .type(JsonFieldType.NUMBER)
                                    .description("응답 코드"),
                                PayloadDocumentation
                                    .fieldWithPath("message")
                                    .type(JsonFieldType.STRING)
                                    .description("응답 메시지")
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
