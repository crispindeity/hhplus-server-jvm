package kr.hhplus.be.server.fake

import kr.hhplus.be.server.application.port.ConcertPort
import kr.hhplus.be.server.domain.Concert

class FakeConcertPort : ConcertPort {
    private val storage: MutableMap<Long, Concert> = mutableMapOf()

    override fun existsConcert(id: Long): Boolean = storage.values.any { it.id == id }

    fun saveSingleConcert(id: Long) {
        storage[id] =
            Concert(
                id = id,
                title = "테스트 콘서트"
            )
    }
}
