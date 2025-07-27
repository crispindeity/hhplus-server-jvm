package kr.hhplus.be.server.payment.adapter.web.dto.response

internal data class PaymentResponse(
    val totalPrice: Long,
    val reservationCount: Int
)
