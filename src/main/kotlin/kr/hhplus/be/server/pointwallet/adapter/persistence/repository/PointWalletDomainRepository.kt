package kr.hhplus.be.server.pointwallet.adapter.persistence.repository

import jakarta.persistence.EntityManager
import kr.hhplus.be.server.pointwallet.adapter.persistence.entity.PointWalletEntity
import org.springframework.stereotype.Repository

@Repository
internal class PointWalletDomainRepository(
    private val entityManager: EntityManager,
    private val jpaRepository: PointWalletJpaRepository
) : PointWalletRepository {
    override fun findWallet(userId: String): PointWalletEntity? = jpaRepository.findByUserId(userId)

    override fun update(entity: PointWalletEntity) {
        entityManager.merge(entity)
    }
}
