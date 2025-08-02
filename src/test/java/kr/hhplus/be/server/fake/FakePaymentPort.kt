package kr.hhplus.be.server.fake

import java.util.UUID
import kr.hhplus.be.server.payment.application.port.PaymentPort
import kr.hhplus.be.server.payment.domain.Payment

internal class FakePaymentPort : PaymentPort {
    private val storage: MutableMap<Long, Payment> = mutableMapOf()
    private var sequence = 0L

    override fun save(payment: Payment): Long {
        if (payment.id == 0L || storage[payment.id] == null) {
            val newPayment: Payment = payment.copy(id = sequence++)
            storage[newPayment.id] = newPayment
            return newPayment.id
        } else {
            storage[payment.id] = payment
            return payment.id
        }
    }

    override fun getAll(paymentIds: List<Long>): List<Payment> =
        paymentIds.mapNotNull { storage[it] }

    override fun update(payment: Payment) {
        storage[payment.id] = payment
    }

    override fun updateStatusToCancelled(ids: List<Long>) {
        ids.forEach { id ->
            val payment: Payment? = storage[id]
            if (payment != null && payment.status == Payment.Status.PENDING) {
                storage[id] = payment.copy(status = Payment.Status.CANCELLED)
            }
        }
    }

    fun saveSinglePayment(userId: UUID) {
        storage[1L] =
            Payment(
                id = 1L,
                userId = userId,
                price = 1000
            )
    }
}
