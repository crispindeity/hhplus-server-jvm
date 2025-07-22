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
    TOKEN_ALREADY_ISSUED(code = 400, message = "token already issued.")
}
