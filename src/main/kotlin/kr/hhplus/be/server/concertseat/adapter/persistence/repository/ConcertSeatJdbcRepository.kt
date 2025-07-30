package kr.hhplus.be.server.concertseat.adapter.persistence.repository

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class ConcertSeatJdbcRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun updateStatusToAvailable(ids: List<Long>): Int {
        val sql = "UPDATE concert_seats SET status = :status WHERE id IN (:ids) AND status = 'HELD'"
        val params: MapSqlParameterSource =
            MapSqlParameterSource()
                .addValue("status", "AVAILABLE")
                .addValue("ids", ids)
        return jdbcTemplate.update(sql, params)
    }
}
