package kr.hhplus.be.server.config

import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.fake.FakeRunner
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
internal class TransactionalTestConfig {
    @Bean
    fun transactional(): Transactional = Transactional(FakeRunner())
}
