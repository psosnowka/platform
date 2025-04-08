package com.task.platform.application.api

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import java.util.*
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UUIDValidator::class])
annotation class ValidUUID(
    val message: String = "Invalid UUID format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class UUIDValidator : ConstraintValidator<ValidUUID, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return try {
            UUID.fromString(value)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}