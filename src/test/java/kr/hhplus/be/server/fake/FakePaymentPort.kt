package kr.hhplus.be.server.fake

import kr.hhplus.be.server.application.port.PaymentPort
import kr.hhplus.be.server.domain.Payment

internal class FakePaymentPort : PaymentPort {
    private val storage: MutableMap<Long, Payment> = mutableMapOf()
    private var sequence = 0L

    override fun save(payment: Payment) {
        if (payment.id == 0L || storage[payment.id] == null) {
            val newPayment: Payment = payment.copy(id = sequence++)
            storage[newPayment.id] = newPayment
        } else {
            storage[payment.id] = payment
        }
    }
}
