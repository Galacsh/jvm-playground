package com.galacsh.spring_context.beans

import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.RootBeanDefinition
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Bean 초기화 및 소멸 시 콜백 메서드를 등록할 수 있습니다.
 * 다음과 같은 방법으로 콜백 메서드를 등록합니다.
 *
 * - [InitializingBean], [DisposableBean] 인터페이스를 구현
 * - `init-method`, `destroy-method` 속성으로 메서드 지정
 */
class BeanInitDestroyCallbackTest {
    private lateinit var beanFactory: DefaultListableBeanFactory

    @BeforeTest
    fun setup() {
        beanFactory = DefaultListableBeanFactory()
    }

    @Test
    @DisplayName("InitializingBean, DisposableBean 을 구현해 초기화, 소멸 콜백 등록을 할 수 있다")
    fun register_init_and_destroy_callback_with_interfaces() {
        // Given
        val definition = RootBeanDefinition(SampleBean::class.java)

        // When
        beanFactory.registerBeanDefinition("sampleBean", definition)
        val bean = beanFactory.getBean(SampleBean::class.java)

        // Then
        assertTrue { bean.afterPropertiesSetCalled }
        assertFalse { bean.destroyCalled }

        // When
        beanFactory.destroyBean("sampleBean", bean)

        // Then
        assertTrue { bean.destroyCalled }
    }

    @Test
    @DisplayName("초기화 메서드와 소멸 메서드를 직접 지정할 수 있다")
    fun specify_init_and_destroy_methods_directly() {
        // Given
        val definition = RootBeanDefinition(SampleBean::class.java).apply {
            initMethodName = "onInit"
            destroyMethodName = "onDestroy"
        }
        beanFactory.registerBeanDefinition("sampleBean", definition)

        // When
        val bean = beanFactory.getBean(SampleBean::class.java)

        // Then
        assertTrue { bean.onInitCalled }
        assertFalse { bean.onDestroyCalled }

        // When
        beanFactory.destroyBean("sampleBean", bean)

        // Then
        assertTrue { bean.onDestroyCalled }
    }

    // ===== 테스트용 클래스 =====

    class SampleBean : InitializingBean, DisposableBean {
        var onInitCalled = false
            private set
        var onDestroyCalled = false
            private set
        var afterPropertiesSetCalled = false
            private set
        var destroyCalled = false
            private set

        @Suppress("unused")
        fun onInit() {
            onInitCalled = true
        }

        @Suppress("unused")
        fun onDestroy() {
            onDestroyCalled = true
        }

        override fun afterPropertiesSet() {
            afterPropertiesSetCalled = true
        }

        override fun destroy() {
            destroyCalled = true
        }
    }
}
