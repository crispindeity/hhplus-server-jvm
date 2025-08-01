package kr.hhplus.be.server.payment.adapter.web

import jakarta.servlet.ServletRequest
import jakarta.servlet.http.HttpServletRequest
import kr.hhplus.be.server.common.adapter.web.dto.ApiResponse
import kr.hhplus.be.server.common.annotation.RequireQueueAccess
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.exception.ServletException
import kr.hhplus.be.server.payment.adapter.web.dto.response.PaymentResponse
import kr.hhplus.be.server.payment.application.service.PaymentService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/payments")
internal class PaymentController(
    private val paymentService: PaymentService
) {
    @PostMapping
    @RequireQueueAccess
    fun payWithPoints(servletRequest: HttpServletRequest): ApiResponse<PaymentResponse> {
        val userId: String =
            servletRequest.getUserIdOrNull()
                ?: throw ServletException(ErrorCode.NOT_FOUND_USER_ID_IN_ATTRIBUTE)
        val response: PaymentResponse = paymentService.payment(userId)
        return ApiResponse.Companion.success(result = response)
    }

    private fun ServletRequest.getUserIdOrNull(): String? = this.getAttribute("userId") as String?
}
