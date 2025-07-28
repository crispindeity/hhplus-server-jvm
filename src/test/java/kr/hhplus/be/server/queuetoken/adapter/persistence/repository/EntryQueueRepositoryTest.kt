package kr.hhplus.be.server.queuetoken.adapter.persistence.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.time.Instant
import java.util.UUID
import kr.hhplus.be.server.TestcontainersConfiguration
import kr.hhplus.be.server.config.jpa.JpaConfig
import kr.hhplus.be.server.queuetoken.adapter.persistence.entity.QueueTokenEntity
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
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(EntryQueueDomainRepository::class, TestcontainersConfiguration::class, JpaConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EntryQueueRepositoryTest {
    @Autowired
    private lateinit var entryQueueRepository: EntryQueueRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

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
    @DisplayName("대기열 토큰 저장소 쿼리 발생 테스트")
    inner class QueryTest {
        @Nested
        @DisplayName("대기열 현재 순번 조회 쿼리 테스트")
        inner class FindCurrentAllowedQueueNumberQueryTest {
            @Test
            @DisplayName("대기열 현재 순번을 조회 할 수 있어야 한다.")
            fun queryTest() {
                // given
                val entity =
                    QueueTokenEntity(
                        userId = UUID.randomUUID().toString(),
                        queueNumber = 1,
                        token = "token",
                        status = QueueTokenEntity.Status.WAITING,
                        expiresAt = Instant.now().plusSeconds(60)
                    )
                testEntityManager.persistAndFlush(entity)

                val stats: Statistics =
                    entityManager.unwrap(Session::class.java).sessionFactory.statistics
                stats.clear()

                // when
                val actual: Int =
                    entryQueueRepository.findCurrentAllowedQueueNumber()

                // then
                Assertions.assertThat(stats.prepareStatementCount).isEqualTo(1)
                Assertions.assertThat(actual).isEqualTo(1)
            }
        }

        @Nested
        @DisplayName("대기열 다음 순번 조회 쿼리 테스트")
        inner class FindEntryQueueNextNumberQueryTest {
            @Test
            @DisplayName("대기열 다음 순번을 조회 할 수 있어야 한다.")
            fun queryTest() {
                // given
                val entity =
                    QueueTokenEntity(
                        userId = UUID.randomUUID().toString(),
                        queueNumber = 1,
                        token = "token",
                        status = QueueTokenEntity.Status.WAITING,
                        expiresAt = Instant.now().plusSeconds(60)
                    )
                testEntityManager.persistAndFlush(entity)

                val stats: Statistics =
                    entityManager.unwrap(Session::class.java).sessionFactory.statistics
                stats.clear()

                // when
                val actual: Int =
                    entryQueueRepository.findEntryQueueNextNumber()

                // then
                Assertions.assertThat(stats.prepareStatementCount).isEqualTo(1)
                Assertions.assertThat(actual).isEqualTo(2)
            }
        }
    }
}
