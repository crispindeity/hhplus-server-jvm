package kr.hhplus.be.server.docs

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.databind.ObjectMapper
import kr.hhplus.be.server.adapter.web.EntryQueueController
import kr.hhplus.be.server.adapter.web.dto.request.EntryQueueTokenRequest
import kr.hhplus.be.server.common.exception.ErrorCode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
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
@WebMvcTest(controllers = [EntryQueueController::class])
class QueueControllerDocsTest {
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
    @DisplayName("[문서] 대기열 토큰 발급 요청")
    fun queueToken() {
        // given
        val request =
            EntryQueueTokenRequest("b3e1f8a4-cfe5-4260-9db3-59d158b6d11e")

        // when
        val result: ResultActions =
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/api/queue/entry-token")
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
                    identifier = "대기열 토큰",
                    resourceDetails =
                        ResourceSnippetParameters
                            .builder()
                            .tag("대기열 토큰")
                            .summary("대기열 토큰 발급 API")
                            .description("대기열 토큰 발급 시 사용되는 API"),
                    snippets =
                        arrayOf(
                            PayloadDocumentation.requestFields(
                                PayloadDocumentation
                                    .fieldWithPath("userId")
                                    .type(JsonFieldType.STRING)
                                    .description("사용자 식별 아이디")
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
                            ),
                            HeaderDocumentation.responseHeaders(
                                HeaderDocumentation
                                    .headerWithName("EntryQueueToken")
                                    .description("대기열 토큰 값")
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
    @DisplayName("[문서] 대기열 토큰 발급 - 잘못된 요청")
    fun queueTokenBadRequest() {
        // given
        val invalidRequest = EntryQueueTokenRequest("")

        // when
        val result =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/api/queue/entry-token")
                    .content(objectMapper.writeValueAsString(invalidRequest))
                    .contentType(MediaType.APPLICATION_JSON)
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
                    "대기열 토큰 - 잘못된 요청",
                    resourceDetails =
                        ResourceSnippetParameters
                            .builder()
                            .tag("대기열 토큰"),
                    snippets =
                        arrayOf(
                            PayloadDocumentation.requestFields(
                                PayloadDocumentation
                                    .fieldWithPath("userId")
                                    .type(JsonFieldType.STRING)
                                    .description("존재하지 않는 사용자 ID")
                            ),
                            PayloadDocumentation.responseFields(
                                PayloadDocumentation
                                    .fieldWithPath("code")
                                    .type(JsonFieldType.NUMBER)
                                    .description("에러 코드"),
                                PayloadDocumentation
                                    .fieldWithPath("message")
                                    .type(JsonFieldType.STRING)
                                    .description("에러 메시지"),
                                PayloadDocumentation
                                    .fieldWithPath("result.errors[0].field")
                                    .type(JsonFieldType.STRING)
                                    .description("잘못 요청한 필드 이름"),
                                PayloadDocumentation
                                    .fieldWithPath("result.errors[0].value")
                                    .type(JsonFieldType.STRING)
                                    .description("잘못 요청한 필드 값")
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
