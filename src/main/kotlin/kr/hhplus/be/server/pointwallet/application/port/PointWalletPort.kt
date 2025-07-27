package kr.hhplus.be.server.pointwallet.application.port

import java.util.UUID
import kr.hhplus.be.server.pointwallet.domain.PointWallet

internal interface PointWalletPort {
    fun update(wallet: PointWallet)

    fun getWallet(userId: UUID): PointWallet?
}
