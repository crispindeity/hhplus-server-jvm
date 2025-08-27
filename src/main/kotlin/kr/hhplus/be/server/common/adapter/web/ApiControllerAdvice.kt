package kr.hhplus.be.server.common.adapter.web

import kr.hhplus.be.server.common.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.exception.LogException
import kr.hhplus.be.server.common.exception.ServletException
import kr.hhplus.be.server.common.lock.RedisLockTimeoutException
import kr.hhplus.be.server.common.lock.RedissonException
import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.transactional.AfterCommitCallbackException
import kr.hhplus.be.server.concert.exception.ConcertException
import kr.hhplus.be.server.concertschedule.exception.ConcertScheduleException
import kr.hhplus.be.server.concertseat.exception.ConcertSeatException
import kr.hhplus.be.server.payment.exception.PaymentException
import kr.hhplus.be.server.pointwallet.exception.PointWalletException
import kr.hhplus.be.server.queuetoken.exception.QueueTokenException
import kr.hhplus.be.server.reservation.exception.ReservationException
import kr.hhplus.be.server.seat.exception.SeatException
import kr.hhplus.be.server.seathold.exception.SeatHoldException
import kr.hhplus.be.server.user.exception.UserException
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

    @ExceptionHandler(ConcertException::class)
    fun concertException(exception: ConcertException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["exception"] = "concertException()"
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(ConcertScheduleException::class)
    fun concertScheduleException(exception: ConcertScheduleException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["exception"] = "concertScheduleException()"
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(ConcertSeatException::class)
    fun concertSeatException(exception: ConcertSeatException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["exception"] = "concertSeatException()"
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(PaymentException::class)
    fun paymentException(exception: PaymentException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["exception"] = "paymentException()"
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(PointWalletException::class)
    fun pointWalletException(exception: PointWalletException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["exception"] = "pointWalletException()"
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(QueueTokenException::class)
    fun queueTokenException(exception: QueueTokenException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["exception"] = "queueTokenException()"
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(ReservationException::class)
    fun reservationException(exception: ReservationException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["exception"] = "reservationException()"
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(SeatException::class)
    fun seatException(exception: SeatException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["exception"] = "seatException()"
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(SeatHoldException::class)
    fun seatHoldException(exception: SeatHoldException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["exception"] = "seatHoldException()"
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(ServletException::class)
    fun servletException(exception: ServletException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["exception"] = "servletException()"
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(UserException::class)
    fun userException(exception: UserException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["exception"] = "userException()"
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(LogException::class)
    fun logException(exception: LogException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["exception"] = "logException()"
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(RedisLockTimeoutException::class)
    fun redisLockTimeOutException(exception: RedisLockTimeoutException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["exception"] = "redisLockTimeOutException()"
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(RedissonException::class)
    fun redissonException(exception: RedissonException): ApiResponse<Unit> =
        Log.logging(logger) { log ->
            log["exception"] = "redissonException()"
            log["message"] = exception.codeInterface.message
            ApiResponse.fail(
                code = exception.codeInterface.code,
                message = exception.message ?: ""
            )
        }

    @ExceptionHandler(AfterCommitCallbackException::class)
    fun afterCommitCallbackException(exception: AfterCommitCallbackException): ApiResponse<Unit> =
        Log.warnLogging(logger) { log ->
            log["exception"] = "afterCommitCallbackException()"
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
