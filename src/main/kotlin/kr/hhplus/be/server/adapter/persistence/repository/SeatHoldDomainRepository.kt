package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.entity.SeatHoldEntity
import org.springframework.stereotype.Repository

@Repository
internal class SeatHoldDomainRepository(
    private val jpaRepository: SeatHoldJpaRepository
) : SeatHoldRepository {
    override fun save(entity: SeatHoldEntity) {
        jpaRepository.save(entity)
    }

    override fun deleteAll(ids: List<Long>) {
        jpaRepository.deleteAllByIdInBatch(ids)
    }
}
