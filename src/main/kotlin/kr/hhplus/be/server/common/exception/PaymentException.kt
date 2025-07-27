package kr.hhplus.be.server.common.exception

class PaymentException(
    val code: ErrorCode,
    message: String? = null
) : BaseException(codeInterface = code, additionalMessage = message)
