package kr.hhplus.be.server.application.port

import java.util.UUID
import kr.hhplus.be.server.domain.PointWallet

internal interface PointWalletPort {
    fun update(wallet: PointWallet)

    fun getWallet(userId: UUID): PointWallet?
}
