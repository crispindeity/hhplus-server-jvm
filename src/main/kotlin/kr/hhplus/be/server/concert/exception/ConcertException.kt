package kr.hhplus.be.server.concert.exception

import kr.hhplus.be.server.common.exception.BaseException
import kr.hhplus.be.server.common.exception.ErrorCode

class ConcertException(
    val code: ErrorCode,
    message: String? = null
) : BaseException(codeInterface = code, additionalMessage = message)
