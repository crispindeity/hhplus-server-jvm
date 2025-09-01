package kr.hhplus.be.server.payment.adapter.event

import kr.hhplus.be.server.payment.application.event.PaymentSaveCompletedEvent
import kr.hhplus.be.server.payment.application.event.PaymentSaveFailedEvent
import kr.hhplus.be.server.payment.application.port.PaymentEventPort
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
internal class PaymentApplicationEventAdapter(
    private val applicationEventPublisher: ApplicationEventPublisher
) : PaymentEventPort {
    override fun savePaymentCompletedEvent(event: PaymentSaveCompletedEvent) {
        applicationEventPublisher.publishEvent(event)
    }

    override fun savePaymentFailEvent(event: PaymentSaveFailedEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}
