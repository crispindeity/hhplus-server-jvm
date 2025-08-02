package kr.hhplus.be.server.payment.adapter.persistence.repository

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class PaymentJdbcRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun updateStatusToCancelled(ids: List<Long>): Int {
        val sql =
            "UPDATE payments SET status = :status WHERE id IN (:ids) AND status = 'PENDING'"
        val params: MapSqlParameterSource =
            MapSqlParameterSource()
                .addValue("status", "CANCELLED")
                .addValue("ids", ids)
        return jdbcTemplate.update(sql, params)
    }
}
