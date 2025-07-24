package kr.hhplus.be.server.docs

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.UUID
import kr.hhplus.be.server.adapter.web.UserPointController
import kr.hhplus.be.server.adapter.web.dto.request.ChargePointsRequest
import kr.hhplus.be.server.application.port.PointWalletPort
import kr.hhplus.be.server.application.port.UserPort
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.config.TestConfig
import kr.hhplus.be.server.domain.PointWallet
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
@WebMvcTest(controllers = [UserPointController::class])
class UserPointControllerDocsTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private val objectMapper = ObjectMapper()

    @Autowired
    private lateinit var userPort: UserPort

    @Autowired
    private lateinit var pointWalletPort: PointWalletPort

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
    @DisplayName("[문서] 유저 포인트 충전 요청")
    fun chargePoints() {
        // given
        val userId: UUID = UUID.randomUUID()
        val request = ChargePointsRequest(1000L)
        val pointWallet = PointWallet(userId = userId, balance = 1000L)

        // mock
        BDDMockito.given(userPort.exists(userId)).willReturn(true)
        BDDMockito.given(pointWalletPort.getWallet(userId)).willReturn(pointWallet)
        BDDMockito.willDoNothing().given(pointWalletPort).chargePoint(pointWallet.chargePoint(500L))

        // when
        val result: ResultActions =
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/api/users/{id}/points/charge", userId)
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
                    identifier = "유저 포인트 충전",
                    resourceDetails =
                        ResourceSnippetParameters
                            .builder()
                            .tag("유저 포인트")
                            .summary("유저 포인트 충전 API")
                            .description("유저 포인트 충전 시 사용되는 API"),
                    snippets =
                        arrayOf(
                            PayloadDocumentation.requestFields(
                                PayloadDocumentation
                                    .fieldWithPath("amount")
                                    .type(JsonFieldType.NUMBER)
                                    .description("포인트 충전 금액")
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
                                    .fieldWithPath("result")
                                    .type(JsonFieldType.NUMBER)
                                    .description("유저 식별자")
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
    @DisplayName("[문서] 유저 포인트 충전 요청 - 잘못된 요청")
    fun chargePointsBadRequest() {
        // given
        val userId: UUID = UUID.randomUUID()
        val request = ChargePointsRequest(-1L)

        // when
        val result: ResultActions =
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/api/users/{id}/points/charge", userId)
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
                    identifier = "유저 포인트 충전 - 잘못된 요청",
                    resourceDetails =
                        ResourceSnippetParameters
                            .builder()
                            .tag("유저 포인트"),
                    snippets =
                        arrayOf(
                            PayloadDocumentation.requestFields(
                                PayloadDocumentation
                                    .fieldWithPath("amount")
                                    .type(JsonFieldType.NUMBER)
                                    .description("포인트 충전 금액")
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

    @Test
    @DisplayName("[문서] 유저 포인트 조회 요청")
    fun findPoint() {
        // given
        val userId: UUID = UUID.randomUUID()
        val pointWallet = PointWallet(userId = userId, balance = 1000L)

        // mock
        BDDMockito.given(userPort.exists(userId)).willReturn(true)
        BDDMockito.given(pointWalletPort.getWallet(userId)).willReturn(pointWallet)

        // when
        val result: ResultActions =
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .get("/api/users/{id}/points", userId)
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
                    identifier = "유저 포인트 조회",
                    resourceDetails =
                        ResourceSnippetParameters
                            .builder()
                            .tag("유저 포인트")
                            .summary("유저 포인트 조회 API")
                            .description("유저 포인트 조회 시 사용되는 API"),
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
                                    .type(JsonFieldType.STRING)
                                    .description("유저 식별자"),
                                PayloadDocumentation
                                    .fieldWithPath("result.balance")
                                    .type(JsonFieldType.NUMBER)
                                    .description("포인트 잔액")
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
