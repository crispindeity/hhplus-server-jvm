package kr.hhplus.be.server.reservation.adapter.persistence.repository

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class ReservationJdbcRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun updateStatusToExpired(ids: List<Long>): Int {
        val sql =
            "UPDATE reservations SET status = :status WHERE id IN (:ids) AND status = 'IN_PROGRESS'"
        val params: MapSqlParameterSource =
            MapSqlParameterSource()
                .addValue("status", "EXPIRED")
                .addValue("ids", ids)
        return jdbcTemplate.update(sql, params)
    }
}
