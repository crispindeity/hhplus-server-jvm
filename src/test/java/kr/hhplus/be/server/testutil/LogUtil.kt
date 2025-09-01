package kr.hhplus.be.server.testutil

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.slf4j.LoggerFactory

fun attachListAppenderFor(
    clazz: Class<*>,
    logLevel: Level
): Pair<Logger, ListAppender<ILoggingEvent>> {
    val logger: Logger = LoggerFactory.getLogger(clazz) as Logger
    logger.level = logLevel

    val appender = ListAppender<ILoggingEvent>()
    appender.start()
    logger.addAppender(appender)

    return logger to appender
}
