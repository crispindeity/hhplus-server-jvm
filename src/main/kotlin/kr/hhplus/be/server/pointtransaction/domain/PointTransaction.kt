package kr.hhplus.be.server.pointtransaction.domain

internal data class PointTransaction(
    val id: Long = 0L,
    val pointWalletId: Long,
    val amount: Long,
    val type: Type
) {
    enum class Type {
        CHARGED,
        USED
    }
}
