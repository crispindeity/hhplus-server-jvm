package kr.hhplus.be.server.ranking.adapter.persistence.repository

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.exception.RedisException
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
internal class RankingDomainRepository(
    private val redisStringTemplate: StringRedisTemplate
) : RankingRepository {
    private val zone = ZoneId.of("Asia/Seoul")

    override fun saveFirstReservationTime(
        concertId: Long,
        concertDate: LocalDate,
        reservationAt: LocalDateTime
    ) {
        val key = "concert:$concertId:$concertDate:firstReservationAt"

        val instant: Instant = reservationAt.atZone(zone).toInstant()
        val value: String = instant.toString()

        redisStringTemplate
            .opsForValue()
            .setIfAbsent(key, value, 7, TimeUnit.DAYS)
    }

    override fun saveSoldOutTime(
        concertId: Long,
        scheduleId: Long,
        concertDate: LocalDate,
        soldOutAt: LocalDateTime
    ) {
        val key = "concert:$concertId:$concertDate:soldOutAt"
        val instant: Instant = soldOutAt.atZone(zone).toInstant()
        val value: String = instant.toString()

        redisStringTemplate
            .opsForValue()
            .setIfAbsent(key, value, 7, TimeUnit.DAYS)
    }

    override fun saveSoldOutRanking(
        concertId: Long,
        scheduleId: Long,
        concertDate: LocalDate
    ) {
        val reservationKey = "concert:$concertId:$concertDate:firstReservationAt"
        val soldOutKey = "concert:$concertId:$concertDate:soldOutAt"
        val reservationAt: Instant? =
            redisStringTemplate.opsForValue()[reservationKey]?.let(Instant::parse)
        val soldOutAt: Instant? =
            redisStringTemplate.opsForValue()[soldOutKey]?.let(Instant::parse)

        if (reservationAt == null || soldOutAt == null) {
            throw RedisException(ErrorCode.NOT_FOUND_REDIS_KEY)
        }
    }
}
