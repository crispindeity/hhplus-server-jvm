package kr.hhplus.be.server.common.lock

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.redisson.config.SingleServerConfig
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig(
    private val redisProperties: RedisProperties
) {
    @Bean
    fun redissonClient(): RedissonClient {
        val config: Config =
            Config().apply {
                val singleServer: SingleServerConfig = useSingleServer()
                singleServer.address = "redis://${redisProperties.host}:${redisProperties.port}"
                singleServer.database = redisProperties.database
                singleServer
                    .apply {
                        redisProperties.username
                            ?.takeIf { it.isNotBlank() }
                            ?.let { singleServer.username = it }
                        redisProperties.password
                            ?.takeIf { it.isNotBlank() }
                            ?.let { singleServer.password = it }
                    }
            }
        return Redisson.create(config)
    }
}
