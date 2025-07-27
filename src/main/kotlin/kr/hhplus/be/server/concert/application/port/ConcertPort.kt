package kr.hhplus.be.server.concert.application.port

internal interface ConcertPort {
    fun existsConcert(id: Long): Boolean
}
