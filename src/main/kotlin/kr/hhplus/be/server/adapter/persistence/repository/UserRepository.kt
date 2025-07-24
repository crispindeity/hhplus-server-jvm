package kr.hhplus.be.server.adapter.persistence.repository

internal interface UserRepository {
    fun exists(userId: String): Boolean
}
