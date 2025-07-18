package com.galacsh.spring_context.aop

import org.aspectj.lang.annotation.*
import org.junit.jupiter.api.DisplayName
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AdvicesTest {

    @Test
    @DisplayName("정상 실행되는 경우 afterThrowing 빼고 모두 호출됨")
    fun normal_execution_calls_all_advices_except_after_throwing() {
        // Given
        val context = AnnotationConfigApplicationContext().apply {
            register(SampleBean::class.java)
            register(SampleAspect::class.java)
            register(AnnotationAwareAspectJAutoProxyCreator::class.java)
            refresh()
        }

        // When
        context.getBean(SampleBean::class.java).sampleMethod()

        // Then
        val expectedLog = listOf(
            "Before (Around)",
            "Before",
            "After Returning",
            "After",
            "After (Around)",
        )
        assertEquals(expectedLog, SampleAspect.log)
    }

    @Test
    @DisplayName("예외가 발생하는 경우 afterThrowing 호출됨")
    fun exception_triggers_after_throwing_advice() {
        // Given
        val context = AnnotationConfigApplicationContext().apply {
            register(SampleBean::class.java)
            register(SampleAspect::class.java)
            register(AnnotationAwareAspectJAutoProxyCreator::class.java)
            refresh()
        }

        // When
        try {
            val throwError = true
            context.getBean(SampleBean::class.java).sampleMethod(throwError)
        } catch (_: Throwable) {
            // do nothing
        }

        // Then
        val expectedLog = listOf(
            "Before (Around)",
            "Before",
            "After Throwing",
            "After",
            "After Throwing (Around)",
            "After (Around)",
        )
        assertEquals(expectedLog, SampleAspect.log)
    }

    @BeforeTest
    fun setup() {
        SampleAspect.log = mutableListOf()
    }

    // ===== 테스트용 클래스 =====

    open class SampleBean {
        open fun sampleMethod(throwError: Boolean = false) {
            if (throwError) {
                throw RuntimeException("Sample exception")
            }
        }
    }

    @Aspect
    class SampleAspect {
        companion object {
            var log: List<String> = mutableListOf()
        }

        @Pointcut("execution(* com.galacsh.spring_context.aop.AdvicesTest.SampleBean.sampleMethod(..))")
        fun sampleMethod() {
            // Pointcut for sampleMethod
        }

        @Before("sampleMethod()")
        fun beforeAdvice() {
            log = log + "Before"
        }

        @AfterReturning("sampleMethod()")
        fun afterReturningAdvice() {
            log = log + "After Returning"
        }

        @AfterThrowing("sampleMethod()", throwing = "ex")
        fun afterThrowingAdvice(ex: Throwable) {
            log = log + "After Throwing"
        }

        @After("sampleMethod()")
        fun afterAdvice() {
            log = log + "After"
        }

        @Around("sampleMethod()")
        fun aroundAdvice(joinPoint: org.aspectj.lang.ProceedingJoinPoint) {
            log = log + "Before (Around)"
            try {
                joinPoint.proceed()
            } catch (e: Throwable) {
                log = log + "After Throwing (Around)"
            }
            log = log + "After (Around)"
        }
    }
}
