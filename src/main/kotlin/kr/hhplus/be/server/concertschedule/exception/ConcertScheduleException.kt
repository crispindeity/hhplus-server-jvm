package kr.hhplus.be.server.concertschedule.exception

import kr.hhplus.be.server.common.exception.BaseException
import kr.hhplus.be.server.common.exception.ErrorCode

class ConcertScheduleException(
    val code: ErrorCode,
    message: String? = null
) : BaseException(codeInterface = code, additionalMessage = message)
