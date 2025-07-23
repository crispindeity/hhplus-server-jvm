package kr.hhplus.be.server

import kr.hhplus.be.server.config.TestConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig::class)
class ServerApplicationTests {
    @Test
    fun contextLoads() {
    }
}
