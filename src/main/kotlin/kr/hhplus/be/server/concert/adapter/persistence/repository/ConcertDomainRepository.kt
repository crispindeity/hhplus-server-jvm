package kr.hhplus.be.server.concert.adapter.persistence.repository

import org.springframework.stereotype.Repository

@Repository
internal class ConcertDomainRepository(
    private val jpaRepository: ConcertJpaRepository
) : ConcertRepository {
    override fun exists(id: Long): Boolean = jpaRepository.existsById(id)
}
