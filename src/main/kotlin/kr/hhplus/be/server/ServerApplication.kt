package kr.hhplus.be.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("kr.hhplus.be.server.config")
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}
