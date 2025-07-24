package kr.hhplus.be.server.adapter.persistence.repository

import kr.hhplus.be.server.adapter.persistence.dto.AvailableSeatProjection
import kr.hhplus.be.server.adapter.persistence.entity.ConcertSeatEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

internal interface ConcertSeatJpaRepository : JpaRepository<ConcertSeatEntity, Long> {
    @Query(
        value = """
            SELECT new kr.hhplus.be.server.adapter.persistence.dto.AvailableSeatProjection(
                cs.id,
                s.number,
                s.price,
                cs.status
            )
            FROM ConcertSeatEntity cs
            JOIN SeatEntity s ON cs.seatId = s.id
            JOIN ConcertScheduleEntity sched ON cs.scheduleId = sched.id
            WHERE sched.concertId = :concertId
              AND cs.status = 'AVAILABLE'
        """
    )
    fun findAvailableSeats(
        @Param("concertId") concertId: Long
    ): List<AvailableSeatProjection>
}
