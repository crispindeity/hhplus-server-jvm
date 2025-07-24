package kr.hhplus.be.server.application.port

internal interface ConcertPort {
    fun existsConcert(id: Long): Boolean
}
