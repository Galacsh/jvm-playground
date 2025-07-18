package com.galacsh.spring_context_usage.aop

import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import org.springframework.aop.MethodBeforeAdvice
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory
import org.springframework.aop.framework.ProxyFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.lang.reflect.Method
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProxyObjectCreationTest {

    /**
     * [ProxyFactory]는 프록시 객체를 생성하는 데 사용되는 Spring AOP 클래스입니다.
     * 다만 이 클래스는 Spring IoC 컨테이너에 의존하지 않고 프록시 객체를 생성하는데 초점을 맞추고 있습니다.
     *
     * Spring IoC 컨테이너에 팩터리 빈으로써 등록하여 프록시 객체 빈을 생성하려면
     * [org.springframework.aop.framework.ProxyFactoryBean]를 사용해야 합니다.
     *
     */
    @Test
    @DisplayName("Programmatic - AOP 프록시 객체를 생성할 수 있다")
    fun create_proxy_object_programmatically() {
        // Given
        val target = SampleBean()
        val advice = InvocationAspect()
        val proxy = ProxyFactory().apply {
            setTarget(target)
            addAdvice(advice)
        }.let { it.proxy as SampleBean }

        // When
        proxy.sampleMethod()

        // Then
        assertEquals(1, InvocationAspect.count)
    }

    @Test
    @DisplayName("Programmatic - Annotation 기반으로 프록시 객체를 생성할 수 있다")
    fun create_proxy_object_with_annotation_programmatically() {
        // Given
        val target = SampleBean()
        val aspect = AnnotationBasedInvocationAspect()
        val proxy = AspectJProxyFactory(target).apply {
            addAspect(aspect::class.java)
        }.let { it.getProxy() as SampleBean }

        // When
        proxy.sampleMethod()

        // Then
        assertEquals(1, AnnotationBasedInvocationAspect.count)
    }

    @Test
    @DisplayName("BeanPostProcessor + Annotation 기반으로 프록시 빈을 얻을 수 있다")
    fun get_proxy_bean_with_bean_post_processor_and_annotation() {
        // Given
        val context = AnnotationConfigApplicationContext().apply {
            register(SampleBean::class.java)
            register(AnnotationBasedInvocationAspect::class.java)
            register(AnnotationAwareAspectJAutoProxyCreator::class.java)
            refresh()
        }
        val proxy = context.getBean(SampleBean::class.java)

        // When
        proxy.sampleMethod()

        // Then
        assertAll(
            { assertTrue { proxy.javaClass.name.contains("SpringCGLIB") } },
            { assertEquals(1, AnnotationBasedInvocationAspect.count) },
        )
    }

    // ===== 테스트용 클래스 =====

    open class SampleBean {
        open fun sampleMethod() {
            // Sample method logic
        }
    }

    class InvocationAspect : MethodBeforeAdvice {
        companion object {
            var count = 0
        }

        override fun before(method: Method, args: Array<out Any>, target: Any?) {
            count++
        }
    }

    @Aspect
    class AnnotationBasedInvocationAspect {
        companion object {
            var count = 0
        }

        @Pointcut("execution(* com.galacsh.spring_context_usage.aop.ProxyObjectCreationTest.SampleBean.sampleMethod(..))")
        fun sampleMethod() {
            // Pointcut for sampleMethod
        }

        @Before("sampleMethod()")
        fun beforeSampleMethod() {
            count++
        }
    }

    @BeforeTest
    fun setup() {
        InvocationAspect.count = 0
        AnnotationBasedInvocationAspect.count = 0
    }
}
