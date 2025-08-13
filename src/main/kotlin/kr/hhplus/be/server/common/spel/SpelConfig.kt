package kr.hhplus.be.server.common.spel

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.expression.spel.standard.SpelExpressionParser

@Configuration
class SpelConfig {
    @Bean
    fun spelExpressionParser(): SpelExpressionParser = SpelExpressionParser()

    @Bean
    fun parameterNameDiscoverer(): DefaultParameterNameDiscoverer = DefaultParameterNameDiscoverer()
}
