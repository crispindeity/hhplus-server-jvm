package kr.hhplus.be.server.adapter.persistence

import kr.hhplus.be.server.adapter.persistence.repository.ConcertRepository
import kr.hhplus.be.server.application.port.ConcertPort
import org.springframework.stereotype.Component

@Component
internal class ConcertPersistenceAdapter(
    private val repository: ConcertRepository
) : ConcertPort {
    override fun existsConcert(id: Long): Boolean = repository.exists(id)
}
