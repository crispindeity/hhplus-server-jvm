package kr.hhplus.be.server.common.adapter.web.dto

import com.fasterxml.jackson.annotation.JsonInclude

class ApiResponse<T> private constructor(
    private val code: Int,
    private val message: String,
    private val result: T? = null
) {
    companion object {
        fun <T> success(
            code: Int = 200,
            message: String = "success",
            result: T? = null
        ): ApiResponse<T> = ApiResponse(code = code, message = message, result = result)

        fun <T> fail(
            code: Int,
            message: String,
            result: T? = null
        ): ApiResponse<T> = ApiResponse(code = code, message = message, result = result)
    }

    fun getCode(): Int = code

    fun getMessage(): String = message

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    fun getResult(): T? = result
}
