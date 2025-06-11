package spring_context_usage.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.junit.jupiter.api.assertThrows
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.annotation.Order
import kotlin.test.Test
import kotlin.test.assertEquals

class RetryExampleTest {
    companion object {
        private const val MAX_RETRIES = 2
    }

    @Test
    fun `에러 2번까지는 재시도 후 성공한다`() {
        // Given
        val context = AnnotationConfigApplicationContext().apply {
            register(SampleBean::class.java)
            register(RetryAspect::class.java)
            register(AnnotationAwareAspectJAutoProxyCreator::class.java)
            refresh()
        }

        // When
        val bean = context.getBean(SampleBean::class.java)
        bean.sampleMethod(
            errorTill = 2
        )

        // Then
        assertEquals(3, bean.nthCall)
    }

    @Test
    fun `3번 이상의 에러는 그대로 에러를 던진다`() {
        // Given
        val context = AnnotationConfigApplicationContext().apply {
            register(SampleBean::class.java)
            register(RetryAspect::class.java)
            register(AnnotationAwareAspectJAutoProxyCreator::class.java)
            refresh()
        }

        // When & Then
        val bean = context.getBean(SampleBean::class.java)
        assertThrows<RuntimeException>("Simulated exception") {
            bean.sampleMethod(
                errorTill = Int.MAX_VALUE
            )
        }
    }

    // ===== 테스트용 클래스 =====

    open class SampleBean {
        open var nthCall = 0

        open fun sampleMethod(errorTill: Int) {
            nthCall++
            if (nthCall <= errorTill) {
                throw RuntimeException("Simulated exception")
            }
        }
    }

    @Aspect
    @Order(1)
    class RetryAspect {
        @Pointcut("execution(* spring_context_usage.aop.RetryExampleTest.SampleBean.sampleMethod(..))")
        fun sampleMethod() {
            // Pointcut for sampleMethod
        }

        @Around("sampleMethod()")
        fun retryOnError(pjp: ProceedingJoinPoint) {
            var numAttempts = 0
            var lastException: Throwable?
            do {
                numAttempts++
                try {
                    pjp.proceed()
                    return // if successful, exit the loop
                } catch (exception: Throwable) {
                    lastException = exception
                }
            } while (numAttempts <= MAX_RETRIES)

            throw lastException!!
        }
    }
}
