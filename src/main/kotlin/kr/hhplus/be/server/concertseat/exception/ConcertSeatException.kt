package kr.hhplus.be.server.concertseat.exception

import kr.hhplus.be.server.common.exception.BaseException
import kr.hhplus.be.server.common.exception.ErrorCode

class ConcertSeatException(
    val code: ErrorCode,
    message: String? = null
) : BaseException(codeInterface = code, additionalMessage = message)
