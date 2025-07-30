package kr.hhplus.be.server.payment.application.port

import kr.hhplus.be.server.payment.domain.Payment

internal interface PaymentPort {
    fun save(payment: Payment): Long

    fun getAll(paymentIds: List<Long>): List<Payment>

    fun update(payment: Payment)

    fun updateStatusToCancelled(ids: List<Long>)
}
