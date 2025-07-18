package com.galacsh.spring_context

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Validator
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator
import org.junit.jupiter.api.DisplayName
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

class ValidationTest {
    @Test
    @DisplayName("팩터리 빈을 통해 생성된 커스텀 검증기로 검증할 수 있다")
    fun validate_with_custom_validator() {
        // Given
        val ac = AnnotationConfigApplicationContext(ValidationConfig::class.java)
        val validator = ac.getBean(Validator::class.java)
        val obj = Sample("invalid")

        // When
        val violations = validator.validate(obj)

        // Then
        assertEquals("Invalid value", violations.first().message)
    }

    @Configuration
    open class ValidationConfig {
        @Bean
        open fun validatorFactory(ac: ApplicationContext): LocalValidatorFactoryBean {
            return LocalValidatorFactoryBean().apply {
                messageInterpolator = ParameterMessageInterpolator()
            }
        }
    }

    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
    @Retention(AnnotationRetention.RUNTIME)
    @Constraint(validatedBy = [Ho::class])
    annotation class Hey(
        val message: String = "Invalid value",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Any>> = []
    )

    class Ho : ConstraintValidator<Hey, String> {
        override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
            return value == "valid"
        }
    }

    class Sample(@Hey val name: String)
}
