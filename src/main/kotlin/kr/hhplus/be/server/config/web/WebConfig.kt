package kr.hhplus.be.server.config.web

import kr.hhplus.be.server.adapter.web.EntryQueueTokenInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@Profile("!test")
class WebConfig(
    private val entryQueueTokenInterceptor: EntryQueueTokenInterceptor
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(entryQueueTokenInterceptor)
            .addPathPatterns(
                "/api/concerts/**",
                "/api/reservations/**",
                "/api/users/**"
            )
    }
}
