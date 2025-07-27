package kr.hhplus.be.server.docs

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.context.ActiveProfiles

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureRestDocs
@ActiveProfiles("test")
annotation class ControllerDocsTest
