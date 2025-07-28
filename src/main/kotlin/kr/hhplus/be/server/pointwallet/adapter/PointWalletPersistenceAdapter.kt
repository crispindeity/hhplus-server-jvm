package kr.hhplus.be.server.pointwallet.adapter

import java.util.UUID
import kr.hhplus.be.server.pointwallet.adapter.persistence.extensions.toDomain
import kr.hhplus.be.server.pointwallet.adapter.persistence.extensions.toEntity
import kr.hhplus.be.server.pointwallet.adapter.persistence.repository.PointWalletRepository
import kr.hhplus.be.server.pointwallet.application.port.PointWalletPort
import kr.hhplus.be.server.pointwallet.domain.PointWallet
import org.springframework.stereotype.Component

@Component
internal class PointWalletPersistenceAdapter(
    private val repository: PointWalletRepository
) : PointWalletPort {
    override fun update(wallet: PointWallet) {
        repository.update(wallet.toEntity())
    }

    override fun getWallet(userId: UUID): PointWallet? =
        repository.findWallet(userId.toString())?.toDomain()
}
