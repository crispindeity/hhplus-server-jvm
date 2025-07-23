package kr.hhplus.be.server.config

import kr.hhplus.be.server.application.port.EntryQueuePort
import kr.hhplus.be.server.application.service.EntryQueueService
import kr.hhplus.be.server.application.service.JWTHelper
import kr.hhplus.be.server.application.service.QueueAccessValidator
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
internal class TestConfig {
    @Bean
    fun entryQueuePort(): EntryQueuePort = mock(EntryQueuePort::class.java)

    @Bean
    fun queueAccessValidator(entryQueuePort: EntryQueuePort): QueueAccessValidator =
        QueueAccessValidator(entryQueuePort)

    @Bean
    fun jwtHelper(): JWTHelper = mock()

    @Bean
    fun entryQueueService(
        entryQueuePort: EntryQueuePort,
        jwtHelper: JWTHelper
    ): EntryQueueService = EntryQueueService(entryQueuePort, jwtHelper)
}
