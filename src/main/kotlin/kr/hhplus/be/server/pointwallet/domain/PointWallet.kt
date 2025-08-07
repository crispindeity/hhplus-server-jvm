package kr.hhplus.be.server.pointwallet.domain

import java.util.UUID
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.pointwallet.exception.PointWalletException

internal data class PointWallet(
    val id: Long = 0L,
    val userId: UUID,
    val balance: Long,
    val version: Int = 0
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
        throw PointWalletException(
            code = ErrorCode.INSUFFICIENT_POINT,
            message = "amount: $amount, balance: ${this.balance}"
        )
    }
}
