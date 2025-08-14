package kr.hhplus.be.server.common.lock

import kr.hhplus.be.server.common.exception.BaseException
import kr.hhplus.be.server.common.exception.ErrorCode

open class RedissonException(
    val code: ErrorCode,
    message: String? = null
) : BaseException(codeInterface = code, additionalMessage = message)

class LockAcquisition(
    message: String? = null
) : RedissonException(ErrorCode.LOCK_BUSY, message)

class RedisLockTimeoutException(
    message: String? = null
) : RedissonException(ErrorCode.LOCK_ACQUIRE_TIME_OUT, message)
