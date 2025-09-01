package kr.hhplus.be.server.payment.application.port

import kr.hhplus.be.server.payment.application.event.PaymentSaveCompletedEvent
import kr.hhplus.be.server.payment.application.event.PaymentSaveFailedEvent

internal interface PaymentEventPort {
    fun savePaymentCompletedEvent(event: PaymentSaveCompletedEvent)

    fun savePaymentFailEvent(event: PaymentSaveFailedEvent)
}
