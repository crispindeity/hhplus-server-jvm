package kr.hhplus.be.server.common.exception

abstract class BaseException(
    val codeInterface: CodeInterface,
    additionalMessage: String? = null
) : RuntimeException(
        additionalMessage?.let { "${codeInterface.message} - $it" }
            ?: codeInterface.message
    )
