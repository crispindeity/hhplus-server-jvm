package kr.hhplus.be.server.adapter.web

import kr.hhplus.be.server.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.log.Log
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@ResponseStatus(HttpStatus.OK)
class ApiControllerAdvice {
    private val logger: Logger = Log.getLogger(ApiControllerAdvice::class.java)

    data class ValidationErrors(
        val errors: List<ValidationError>
    )

    data class ValidationError(
        val field: String?,
        val value: Any?
    ) {
        companion object {
            fun of(filedError: FieldError): ValidationError =
                ValidationError(filedError.field, filedError.rejectedValue)
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidExceptionHandle(
        exception: MethodArgumentNotValidException
    ): ApiResponse<ValidationErrors> =
        Log.warnLogging(logger) { log ->
            val errors: List<ValidationError> =
                exception.bindingResult.fieldErrors
                    .stream()
                    .map(ValidationError::of)
                    .toList()

            log["message"] = exception.message

            return@warnLogging ApiResponse.fail(
                code = ErrorCode.INVALID_REQUEST_VALUE.code,
                message = ErrorCode.INVALID_REQUEST_VALUE.message,
                result = ValidationErrors(errors)
            )
        }

    @ExceptionHandler(CustomException::class)
    fun handlerCustomException(exception: CustomException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ApiResponse<Unit> =
        Log.errorLogging(logger, exception) { log ->
            log["exception message"] = exception.message.toString()
            ApiResponse.fail(500, "internal server error")
        }
}
