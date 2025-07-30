package kr.hhplus.be.server.seathold.adapter.persistence.repository

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class SeatHoldJdbcRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun deleteAllByConcertSeatId(concertSeatIds: List<Long>) {
        val sql = "DELETE FROM seat_holds WHERE concert_seat_id IN (:concertSeatIds)"
        val params =
            MapSqlParameterSource()
                .addValue("concertSeatIds", concertSeatIds)

        jdbcTemplate.update(sql, params)
    }
}
