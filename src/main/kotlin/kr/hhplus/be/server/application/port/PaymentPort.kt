package kr.hhplus.be.server.application.port

import kr.hhplus.be.server.domain.Payment

internal interface PaymentPort {
    fun save(payment: Payment)
}
