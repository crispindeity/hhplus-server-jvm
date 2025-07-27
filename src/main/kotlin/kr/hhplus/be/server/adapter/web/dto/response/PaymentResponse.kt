package kr.hhplus.be.server.adapter.web.dto.response

internal data class PaymentResponse(
    val totalPrice: Long,
    val reservationCount: Int
)
