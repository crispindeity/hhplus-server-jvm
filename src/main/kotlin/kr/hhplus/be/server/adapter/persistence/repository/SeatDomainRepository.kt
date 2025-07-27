package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.entity.SeatEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class SeatDomainRepository(
    private val jpaRepository: SeatJpaRepository
) : SeatRepository {
    override fun findBy(id: Long): SeatEntity? = jpaRepository.findByIdOrNull(id)

    override fun findAllBy(seatIds: List<Long>): List<SeatEntity> =
        jpaRepository.findAllByIdIn(seatIds)
}
