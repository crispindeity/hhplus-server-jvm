package kr.hhplus.be.server.concertschedule.adapter.persistence.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.hhplus.be.server.TestcontainersConfiguration
import kr.hhplus.be.server.concertschedule.adapter.persistence.entity.ConcertScheduleEntity
import org.assertj.core.api.Assertions
import org.hibernate.Session
import org.hibernate.stat.Statistics
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(ConcertScheduleDomainRepository::class, TestcontainersConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ConcertScheduleRepositoryTest {
    @Autowired
    private lateinit var concertScheduleRepository: ConcertScheduleRepository

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @BeforeEach
    fun setUp() {
        val stats: Statistics =
            entityManager.unwrap(Session::class.java).sessionFactory.statistics
        stats.isStatisticsEnabled = true
        stats.clear()
    }

    @Nested
    @DisplayName("콘서트 스케쥴 저장소 쿼리 발생 테스트")
    inner class QueryTest {
        @Nested
        @DisplayName("예약 가능한 콘서트 날짜 조회 쿼리 테스트")
        inner class AvailableScheduleQueryTest {
            @Test
            @DisplayName("예약 가능한 콘서트 날짜를 조회 할 수 있어야 한다.")
            fun queryTest() {
                // given
                val concertId = 1L

                val stats: Statistics =
                    entityManager.unwrap(Session::class.java).sessionFactory.statistics
                stats.clear()
                // when
                val actual: List<ConcertScheduleEntity> =
                    concertScheduleRepository.findAvailableSchedules(concertId)

                // then
                Assertions.assertThat(stats.prepareStatementCount).isEqualTo(1)
                Assertions.assertThat(actual.size).isNotZero
            }
        }
    }
}
