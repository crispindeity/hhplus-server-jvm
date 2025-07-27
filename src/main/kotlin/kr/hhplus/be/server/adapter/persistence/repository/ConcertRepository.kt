package kr.hhplus.be.server.adapter.persistence.repository

internal interface ConcertRepository {
    fun exists(id: Long): Boolean
}
