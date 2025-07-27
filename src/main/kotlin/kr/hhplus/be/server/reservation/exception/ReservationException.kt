package kr.hhplus.be.server.reservation.exception

import kr.hhplus.be.server.common.exception.BaseException
import kr.hhplus.be.server.common.exception.ErrorCode

class ReservationException(
    val code: ErrorCode,
    message: String? = null
) : BaseException(codeInterface = code, additionalMessage = message)
