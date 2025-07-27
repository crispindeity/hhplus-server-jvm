package kr.hhplus.be.server.queuetoken.exception

import kr.hhplus.be.server.common.exception.BaseException
import kr.hhplus.be.server.common.exception.ErrorCode

class QueueTokenException(
    val code: ErrorCode,
    message: String? = null
) : BaseException(codeInterface = code, additionalMessage = message)
