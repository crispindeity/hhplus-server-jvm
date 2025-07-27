package kr.hhplus.be.server.fake

import java.util.UUID
import kr.hhplus.be.server.pointwallet.application.port.PointWalletPort
import kr.hhplus.be.server.pointwallet.domain.PointWallet

internal class FakePointWalletPort : PointWalletPort {
    private val storage: MutableMap<Long, PointWallet> = mutableMapOf()

    override fun update(wallet: PointWallet) {
        storage[wallet.id] = wallet
    }

    override fun getWallet(userId: UUID): PointWallet? = storage.values.find { it.userId == userId }

    fun saveSingleWallet(
        userId: UUID,
        point: Long = 1000
    ) {
        storage[1L] =
            PointWallet(
                id = 1L,
                userId = userId,
                balance = point
            )
    }
}
