package kr.hhplus.be.server.seathold.adapter.persistence.repository

import kr.hhplus.be.server.seathold.adapter.persistence.entity.SeatHoldEntity
import org.springframework.stereotype.Repository

@Repository
internal class SeatHoldDomainRepository(
    private val jpaRepository: SeatHoldJpaRepository,
    private val jdbcRepository: SeatHoldJdbcRepository
) : SeatHoldRepository {
    override fun save(entity: SeatHoldEntity) {
        jpaRepository.save(entity)
    }

    override fun deleteAll(ids: List<Long>) {
        jpaRepository.deleteAllByIdInBatch(ids)
    }

    override fun deleteAllByConcertSeatIds(concertSeatIds: List<Long>) {
        jdbcRepository.deleteAllByConcertSeatId(concertSeatIds)
    }
}
