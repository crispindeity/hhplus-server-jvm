package kr.hhplus.be.server.queuetoken.adapter.init

import kr.hhplus.be.server.common.log.Log
import kr.hhplus.be.server.common.transactional.Transactional
import kr.hhplus.be.server.queuetoken.adapter.persistence.entity.QueueNumberEntity
import kr.hhplus.be.server.queuetoken.adapter.persistence.repository.EntryQueueNumberJpaRepository
import org.slf4j.Logger
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class QueueNumberInitializer(
    private val repository: EntryQueueNumberJpaRepository,
    private val transactional: Transactional
) {
    private val logger: Logger = Log.getLogger(QueueNumberInitializer::class.java)

    companion object {
        const val ID = "entry_queue"
        const val NUMBER = 1
    }

    @EventListener(ApplicationReadyEvent::class)
    fun initialize() =
        Log.logging(logger) { log ->
            log["method"] = "initialize()"
            transactional.run {
                if (!repository.existsById(ID)) {
                    repository.save(QueueNumberEntity(ID, NUMBER))
                }
            }
        }
}
