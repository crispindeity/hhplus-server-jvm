package kr.hhplus.be.server.docs

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.databind.ObjectMapper
import kr.hhplus.be.server.adapter.web.dto.request.PayWithPointsRequest
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
class PaymentControllerDocsTest {
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
    @DisplayName("[문서] 포인트 결제 요청")
    fun paymentPoints() {
        // given
        val reservationId = 1L
        val request = PayWithPointsRequest(1000L)

        // when
        val result: ResultActions =
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/api/reservations/{id}/payment/points", reservationId)
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
                                    .fieldWithPath("result.userId")
                                    .type(JsonFieldType.NUMBER)
                                    .description("유저 식별자"),
                                PayloadDocumentation
                                    .fieldWithPath("result.reservationId")
                                    .type(JsonFieldType.NUMBER)
                                    .description("예약 식별자"),
                                PayloadDocumentation
                                    .fieldWithPath("result.price")
                                    .type(JsonFieldType.NUMBER)
                                    .description("가격"),
                                PayloadDocumentation
                                    .fieldWithPath("result.paidAt")
                                    .type(JsonFieldType.STRING)
                                    .description("결제 일자")
                            ),
                            PayloadDocumentation.requestFields(
                                PayloadDocumentation
                                    .fieldWithPath("price")
                                    .type(JsonFieldType.NUMBER)
                                    .description("가격")
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

    @Test
    @DisplayName("[문서] 포인트 결제 요청")
    fun paymentPointsBadRequest() {
        // given
        val reservationId = 1L
        val request = PayWithPointsRequest(-1L)

        // when
        val result: ResultActions =
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/api/reservations/{id}/payment/points", reservationId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("EntryQueueToken", "token")
                )

        // then
        result
            .andExpectAll(
                MockMvcResultMatchers.status().isBadRequest,
                MockMvcResultMatchers.jsonPath("$.code").value(400),
                MockMvcResultMatchers.jsonPath("$.message").value("bad request")
            ).andDo(
                MockMvcRestDocumentationWrapper.document(
                    identifier = "포인트 결제 - 잘못된 요청",
                    resourceDetails =
                        ResourceSnippetParameters
                            .builder()
                            .tag("결제"),
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
                                    .description("응답 메시지")
                            ),
                            PayloadDocumentation.requestFields(
                                PayloadDocumentation
                                    .fieldWithPath("price")
                                    .type(JsonFieldType.NUMBER)
                                    .description("가격")
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
