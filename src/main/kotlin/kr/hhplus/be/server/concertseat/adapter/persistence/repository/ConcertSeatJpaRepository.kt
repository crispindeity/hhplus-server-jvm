package kr.hhplus.be.server.concertseat.adapter.persistence.repository

import kr.hhplus.be.server.concertseat.adapter.persistence.dto.AvailableSeatProjection
import kr.hhplus.be.server.concertseat.adapter.persistence.entity.ConcertSeatEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

internal interface ConcertSeatJpaRepository : JpaRepository<ConcertSeatEntity, Long> {
    @Query(
        value = """
            SELECT new kr.hhplus.be.server.concertseat.adapter.persistence.dto.AvailableSeatProjection(
                cs.id,
                s.number,
                s.price,
                cs.status
            )
            FROM ConcertSeatEntity cs
            JOIN SeatEntity s ON cs.seatId = s.id
            JOIN ConcertScheduleEntity sched ON cs.scheduleId = sched.id
            WHERE sched.id = :id
              AND cs.status = 'AVAILABLE'
        """
    )
    fun findAvailableSeats(
        @Param("id") id: Long
    ): List<AvailableSeatProjection>
}
