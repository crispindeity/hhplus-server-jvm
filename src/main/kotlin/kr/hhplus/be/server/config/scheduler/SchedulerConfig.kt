package kr.hhplus.be.server.config.scheduler

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@Profile("!test")
class SchedulerConfig
