package kr.hhplus.be.server.domain

import java.util.UUID

internal data class PointWallet(
    val id: Long = 0L,
    val userId: UUID,
    val balance: Long
) {
    fun chargePoint(amount: Long): PointWallet =
        this.copy(
            balance = balance + amount
        )
}
