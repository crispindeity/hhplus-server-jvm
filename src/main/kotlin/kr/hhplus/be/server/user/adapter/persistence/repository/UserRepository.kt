package kr.hhplus.be.server.user.adapter.persistence.repository

internal interface UserRepository {
    fun exists(userId: String): Boolean
}
