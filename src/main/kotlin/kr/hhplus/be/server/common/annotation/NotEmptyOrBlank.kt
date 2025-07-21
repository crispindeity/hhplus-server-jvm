package kr.hhplus.be.server.common.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [NotEmptyOrBlankValidator::class])
annotation class NotEmptyOrBlank(
    val message: String = "must not be empty or blank",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
