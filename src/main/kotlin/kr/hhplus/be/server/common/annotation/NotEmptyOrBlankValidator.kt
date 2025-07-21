package kr.hhplus.be.server.common.annotation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class NotEmptyOrBlankValidator : ConstraintValidator<NotEmptyOrBlank, String> {
    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext?
    ): Boolean = value == null || value.trim().isNotEmpty()
}
