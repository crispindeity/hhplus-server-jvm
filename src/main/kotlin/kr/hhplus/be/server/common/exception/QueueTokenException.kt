package kr.hhplus.be.server.common.exception

class QueueTokenException(
    val code: ErrorCode,
    message: String? = null
) : BaseException(codeInterface = code, additionalMessage = message)
