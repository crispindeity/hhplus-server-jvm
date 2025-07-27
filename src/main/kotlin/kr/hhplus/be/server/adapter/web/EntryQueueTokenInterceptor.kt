package kr.hhplus.be.server.adapter.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jwt.JWTClaimsSet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.hhplus.be.server.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.application.service.JWTHelper
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import org.slf4j.Logger
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
@Profile("!test")
class EntryQueueTokenInterceptor(
    private val jwtHelper: JWTHelper,
    private val objectMapper: ObjectMapper
) : HandlerInterceptor {
    private val logger: Logger = Log.getLogger(EntryQueueTokenInterceptor::class.java)

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (request.method.equals("OPTIONS", ignoreCase = true)) {
            response.status = HttpServletResponse.SC_OK
            return true
        }

        val token: String? = extractToken(request)

        if (token.isNullOrBlank()) {
            writeErrorResponse(response, ErrorCode.MISSING_ENTRY_QUEUE_JWT_HEADER)
            return false
        }

        return try {
            val claims: JWTClaimsSet = jwtHelper.parseJWT(token)
            request.setAttribute("userId", claims.getStringClaim("userId"))
            request.setAttribute("queueNumber", claims.getStringClaim("queueNumber"))
            true
        } catch (exception: Exception) {
            return Log.errorLogging(logger, exception) { log ->
                log["exception message"] = exception.message ?: "unknown error"
                writeErrorResponse(response, ErrorCode.INVALID_JWT)
                false
            }
        }
    }

    private fun extractToken(request: HttpServletRequest): String? =
        request.getHeader("EntryQueueToken")

    private fun writeErrorResponse(
        response: HttpServletResponse,
        errorCode: ErrorCode
    ) {
        val apiResponse: ApiResponse<Unit> =
            ApiResponse.fail(
                code = errorCode.code,
                message = errorCode.message
            )
        response.status = HttpServletResponse.SC_OK
        response.writer.write(objectMapper.writeValueAsString(apiResponse))
    }
}
