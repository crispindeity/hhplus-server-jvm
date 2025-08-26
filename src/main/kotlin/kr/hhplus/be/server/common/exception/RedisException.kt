package kr.hhplus.be.server.common.exception

class RedisException(
    val code: ErrorCode,
    message: String? = null
) : BaseException(codeInterface = code, additionalMessage = message)
