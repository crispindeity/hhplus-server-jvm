package kr.hhplus.be.server.scheduler.job

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import java.time.LocalDateTime
import kr.hhplus.be.server.scheduler.orchestrator.ReservationOrchestrator
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
internal class ExpiredReservationJob(
    private val orchestrator: ReservationOrchestrator
) {
    private val log = LoggerFactory.getLogger(ExpiredReservationJob::class.java)

    @Scheduled(cron = "0 * * * * *")
    fun run() {
        val currentDateTime: LocalDateTime = LocalDateTime.now()
        orchestrator.expireReservations(currentDateTime)
    }

    @PostConstruct
    fun onInit() {
        log.info("🟢ExpiredReservationJob scheduler initialized. Runs every minute.")
    }

    @PreDestroy
    fun onShutdown() {
        log.info("🛑ExpiredReservationJob scheduler is shutting down.")
    }
}
