package kr.hhplus.be.server.concert.adapter.persistence

import kr.hhplus.be.server.concert.adapter.persistence.repository.ConcertRepository
import kr.hhplus.be.server.concert.application.port.ConcertPort
import org.springframework.stereotype.Component

@Component
internal class ConcertPersistenceAdapter(
    private val repository: ConcertRepository
) : ConcertPort {
    override fun existsConcert(id: Long): Boolean = repository.exists(id)
}
