package kr.hhplus.be.server.config.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JWTProperties(
    val secret: String,
    val expirationMinutes: Long
)
