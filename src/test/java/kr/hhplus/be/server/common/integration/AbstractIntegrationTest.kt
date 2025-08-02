package kr.hhplus.be.server.common.integration

import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort

@IntegrationTest
abstract class AbstractIntegrationTest {
    @Autowired
    private lateinit var databaseCleanup: DatabaseCleanup

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun setup() {
        RestAssured.port
            .takeIf { it == RestAssured.UNDEFINED_PORT }
            ?.let {
                RestAssured.port = port
                databaseCleanup.afterPropertiesSet()
            }
        databaseCleanup.execute()
    }
}
