package kr.hhplus.be.server.common.exception

interface CodeInterface {
    val code: Int
    val message: String
}

enum class ErrorCode(
    override val code: Int,
    override val message: String
) : CodeInterface {
    FAILED_TO_INVOKE_IN_LOG(code = 100, message = "failed to invoke in log."),
    INVALID_REQUEST_VALUE(code = 400, message = "invalid request value."),
    TOKEN_ALREADY_ISSUED(code = 400, message = "token already issued."),
    MISSING_ENTRY_QUEUE_JWT_HEADER(code = 400, message = "missing entry queue token header."),
    INVALID_JWT(code = 403, message = "invalid jwt."),
    QUEUE_NOT_YET_ALLOWED(code = 400, message = "queue not yet allowed."),
    INVALID_QUEUE_TOKEN(code = 400, message = "invalid queue token."),
    NOT_FOUND_QUEUE_TOKEN(code = 400, message = "not found queue token."),
    QUEUE_TOKEN_INVALID_STATUS(code = 400, message = "invalid token status."),
    NO_REQUEST_CONTEXT(code = 400, message = "no request context."),
    MISSING_REQUEST_ATTRIBUTE(code = 400, message = "missing request attribute."),
    NOT_FOUND_CONCERT(code = 404, message = "not found concert."),
    NOT_FOUND_CONCERT_SCHEDULE(code = 404, message = "not found concert schedule."),
    NOT_FOUND_CONCERT_SEAT(code = 404, message = "not found concert seat."),
    INVALID_CONCERT_DATE(code = 400, message = "invalid concert date."),
    ALREADY_RESERVED(code = 400, message = "already reserved."),
    NOT_FOUND_USER_ID_IN_ATTRIBUTE(code = 404, message = "not found user id in attribute."),
    NOT_FOUND_SEAT(code = 404, message = "not found seat."),
    NOT_FOUND_USER(code = 404, message = "not found user."),
    NOT_FOUND_USER_POINT_WALLET(code = 404, message = "not found user point wallet."),
    NOT_FOUND_RESERVATION(code = 404, message = "not found reservation."),
    NOT_FOUND_PAYMENT_INFO(code = 404, message = "not found payment info."),
    ALREADY_PAYMENT(code = 400, message = "already payment"),
    INSUFFICIENT_POINT(code = 400, message = "not enough point to complete the operation"),
    INVALID_STATUS(code = 400, message = "invalid status."),
    FAILED_RETRY(code = 409, message = "optimistic lock failed after retries."),
    LOCK_BUSY(code = 409, message = "lock acquisition failed due to concurrent request."),
    DUPLICATE_PAYMENT_ATTEMPT(code = 409, message = "duplicate payment request detected."),
    LOCK_ACQUIRE_TIME_OUT(code = 500, message = "locking out of time."),
    NOT_VALUE_IN_EXPRESSION(code = 500, message = "not value in expression."),
    INVALID_RETURN_TYPE(code = 500, message = "invalid return types"),
    KEYS_IS_EMPTY(code = 404, message = "is empty keys."),
    FAILED_TO_UNLOCK(code = 500, message = "failed to unlock."),
    FAILED_AFTER_COMMIT_CALLBACK(code = 500, message = "failed to after commit callback."),
    FAILED_SEND_RESERVATION_INFO(code = 500, message = "failed to send reservation info.")
}
