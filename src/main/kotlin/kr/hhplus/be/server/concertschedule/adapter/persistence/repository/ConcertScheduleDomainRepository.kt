package kr.hhplus.be.server.concertschedule.adapter.persistence.repository

import java.time.LocalDate
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.exception.RedisException
import kr.hhplus.be.server.concertschedule.adapter.persistence.entity.ConcertScheduleEntity
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class ConcertScheduleDomainRepository(
    private val jpaRepository: ConcertScheduleJpaRepository,
    private val redisTemplate: RedisTemplate<String, Any?>
) : ConcertScheduleRepository {
    override fun findAvailableSchedules(concertId: Long): List<ConcertScheduleEntity> =
        jpaRepository.findAvailableSchedules(concertId)

    override fun findSchedule(
        concertId: Long,
        date: LocalDate
    ): ConcertScheduleEntity = jpaRepository.findByConcertIdAndDate(concertId, date)

    override fun findSchedule(scheduleId: Long): ConcertScheduleEntity? =
        jpaRepository.findByIdOrNull(scheduleId)

    override fun decreaseSeatCount(
        concertId: Long,
        scheduleId: Long
    ): Long {
        val key = "count:concert_schedule:available:$concertId:$scheduleId"
        val remaining: Long =
            redisTemplate.opsForValue().decrement(key)
                ?: throw RedisException(ErrorCode.NOT_FOUND_REDIS_KEY)
        return remaining
    }
}
