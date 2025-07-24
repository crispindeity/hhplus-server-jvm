package kr.hhplus.be.server.fake

import java.util.UUID
import kr.hhplus.be.server.application.port.PointWalletPort
import kr.hhplus.be.server.domain.PointWallet

internal class FakePointWalletPort : PointWalletPort {
    private val storage: MutableMap<Long, PointWallet> = mutableMapOf()

    override fun chargePoint(wallet: PointWallet) {
        storage[wallet.id] = wallet
    }

    override fun getWallet(userId: UUID): PointWallet? = storage.values.find { it.userId == userId }

    fun saveSingleWallet(
        userId: UUID,
        point: Long = 1000
    ) {
        storage[1L] =
            PointWallet(
                userId = userId,
                balance = point
            )
    }
}
