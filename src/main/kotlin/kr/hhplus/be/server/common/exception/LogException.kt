package kr.hhplus.be.server.common.exception

class LogException(
    val code: ErrorCode,
    message: String? = null
) : BaseException(codeInterface = code, additionalMessage = message)
