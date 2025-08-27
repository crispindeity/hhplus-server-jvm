package kr.hhplus.be.server.common.transactional

import kr.hhplus.be.server.common.exception.BaseException
import kr.hhplus.be.server.common.exception.ErrorCode

class AfterCommitCallbackException(
    code: ErrorCode,
    message: String? = null
) : BaseException(codeInterface = code, additionalMessage = message)
