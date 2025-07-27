package kr.hhplus.be.server.adapter.web

import jakarta.servlet.http.HttpServletRequest
import java.util.UUID
import kr.hhplus.be.server.application.service.QueueAccessValidator
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.exception.ServletException
import kr.hhplus.be.server.common.log.Log
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.Logger
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
internal class QueueAccessAspect(
    private val queueAccessValidator: QueueAccessValidator
) {
    private val logger: Logger = Log.getLogger(QueueAccessAspect::class.java)

    @Around("@annotation(kr.hhplus.be.server.common.annotation.RequireQueueAccess)")
    fun validateQueueAccess(joinPoint: ProceedingJoinPoint): Any? =
        Log.logging(logger) { log ->
            log["method"] = "validateQueueAccess()"
            log["joinPoint"] = joinPoint.signature.name
            val request: HttpServletRequest =
                (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
                    ?: throw ServletException(ErrorCode.NO_REQUEST_CONTEXT)

            val userId: String = request.requireAttr("userId")
            val queueNumber: Int = request.requireAttr("queueNumber")

            queueAccessValidator.validateQueueToken(UUID.fromString(userId), queueNumber)
            joinPoint.proceed()
        }
}

inline fun <reified T> HttpServletRequest.requireAttr(name: String): T =
    this.getAttribute(name) as? T
        ?: throw ServletException(ErrorCode.MISSING_REQUEST_ATTRIBUTE, name)
