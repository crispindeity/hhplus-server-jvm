package kr.hhplus.be.server.config

import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.queuetoken.application.port.EntryQueuePort
import kr.hhplus.be.server.queuetoken.application.service.EntryQueueService
import kr.hhplus.be.server.queuetoken.application.service.JWTHelper
import kr.hhplus.be.server.queuetoken.application.service.QueueAccessValidator
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
internal class EntryQueueTestConfig {
    @Bean
    fun entryQueuePort(): EntryQueuePort = mock(EntryQueuePort::class.java)

    @Bean
    fun queueAccessValidator(
        entryQueuePort: EntryQueuePort,
        transactional: Transactional
    ): QueueAccessValidator = QueueAccessValidator(entryQueuePort, transactional)

    @Bean
    fun jwtHelper(): JWTHelper = mock()

    @Bean
    fun entryQueueService(
        entryQueuePort: EntryQueuePort,
        jwtHelper: JWTHelper,
        transactional: Transactional
    ): EntryQueueService = EntryQueueService(entryQueuePort, jwtHelper, transactional)
}
