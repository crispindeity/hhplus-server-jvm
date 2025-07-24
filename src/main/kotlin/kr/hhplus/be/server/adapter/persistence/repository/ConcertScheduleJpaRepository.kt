package kr.hhplus.be.server.adapter.persistence.repository

import java.time.LocalDate
import kr.hhplus.be.server.adapter.persistence.entity.ConcertScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

internal interface ConcertScheduleJpaRepository : JpaRepository<ConcertScheduleEntity, Long> {
    @Query(
        value = """
            SELECT s.id, s.concert_id, s.date, s.created_at, s.updated_at
            FROM concert_schedules s
            WHERE s.concert_id = :concertId
              AND EXISTS (
                  SELECT 1
                  FROM concert_seats cs
                  WHERE cs.schedule_id = s.id
                    AND cs.status = 'AVAILABLE'
              )
        """,
        nativeQuery = true
    )
    fun findAvailableSchedules(
        @Param("concertId") concertId: Long
    ): List<ConcertScheduleEntity>

    fun findByConcertIdAndDate(
        concertId: Long,
        date: LocalDate
    ): ConcertScheduleEntity
}
