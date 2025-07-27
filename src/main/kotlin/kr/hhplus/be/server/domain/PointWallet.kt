package kr.hhplus.be.server.domain

import java.util.UUID
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode

internal data class PointWallet(
    val id: Long = 0L,
    val userId: UUID,
    val balance: Long
) {
    fun chargePoint(amount: Long): PointWallet =
        this.copy(
            balance = balance + amount
        )

    fun usePoint(amount: Long): PointWallet {
        verifySufficientPoint(amount)
        return this.copy(
            balance = balance - amount
        )
    }

    private fun verifySufficientPoint(amount: Long) {
        if (amount >= 0 && this.balance - amount >= 0) {
            return
        }
        throw CustomException(
            codeInterface = ErrorCode.INSUFFICIENT_POINT,
            additionalMessage = "amount: $amount, balance: ${this.balance}"
        )
    }
}
