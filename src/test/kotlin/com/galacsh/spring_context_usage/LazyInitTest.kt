package com.galacsh.spring_context_usage

import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LazyInitTest {
    @Test
    @DisplayName("lazy 가 아닌 경우, 미리 초기화 됨")
    fun eager_initialization_without_lazy() {
        var initialized = false

        // Given
        val context = AnnotationConfigApplicationContext()
        context.registerBeanDefinition(
            "sampleBean",
            RootBeanDefinition(SampleBean::class.java).apply {
                constructorArgumentValues.addIndexedArgumentValue(0, { initialized = true })
            }
        )

        // When
        context.refresh()

        // Then
        assertTrue { initialized }
    }

    @Test
    @DisplayName("lazy로 설정된 경우, 초기화가 지연됨")
    fun lazy_initialization_with_lazy_setting() {
        var initialized = false

        // Given
        val context = AnnotationConfigApplicationContext()
        context.registerBeanDefinition(
            "sampleBean",
            RootBeanDefinition(SampleBean::class.java).apply {
                isLazyInit = true
                constructorArgumentValues.addIndexedArgumentValue(0, { initialized = true })
            }
        )

        // When - before getting the bean
        context.refresh()

        // Then - should not be initialized yet
        assertFalse { initialized }

        // When - after getting the bean
        context.getBean(SampleBean::class.java)

        // Then - should be initialized now
        assertTrue { initialized }
    }

    // ===== 테스트용 클래스 =====

    class SampleBean(private val onInit: () -> Unit) : InitializingBean {
        override fun afterPropertiesSet() {
            onInit()
        }
    }
}
