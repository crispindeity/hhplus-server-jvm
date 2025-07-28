package kr.hhplus.be.server.concert.adapter.persistence.repository

internal interface ConcertRepository {
    fun exists(id: Long): Boolean
}
