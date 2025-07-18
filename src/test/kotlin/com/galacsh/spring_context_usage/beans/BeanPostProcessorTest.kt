package com.galacsh.spring_context_usage.beans

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.RootBeanDefinition
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * BeanPostProcessor 는 빈이 초기화된 후에 개입하여 빈을 커스터마이즈하거나
 * 다른 로직을 적용할 수 있는 기능을 제공합니다.
 *
 * - postProcessBeforeInitialization: 빈 초기화 전에 호출
 * - postProcessAfterInitialization: 빈 초기화 후에 호출
 *
 * @see org.springframework.beans.factory.config.BeanPostProcessor
 */
class BeanPostProcessorTest {
    private lateinit var beanFactory: DefaultListableBeanFactory

    @BeforeTest
    fun setup() {
        beanFactory = DefaultListableBeanFactory()
    }

    @Test
    fun `후처리기가 없는 경우 빈은 그대로 생성된다`() {
        // Given
        val definition = RootBeanDefinition(SampleBean::class.java)
        beanFactory.registerBeanDefinition("sampleBean", definition)

        // When
        val withoutPostProcessor = beanFactory.getBean(SampleBean::class.java)

        // Then
        assertFalse { withoutPostProcessor.postProcessed }
    }

    @Test
    fun `후처리기가 있는 경우 특정 시점에 개입해 커스터마이즈하거나 다른 로직을 적용할 수 있다`() {
        // Given
        val definition = RootBeanDefinition(SampleBean::class.java)
        beanFactory.registerBeanDefinition("sampleBean", definition)

        val postProcessor = SampleBeanPostProcessor()
        beanFactory.addBeanPostProcessor(postProcessor)

        // When
        val withPostProcessor = beanFactory.getBean(SampleBean::class.java)

        // Then
        assertTrue { withPostProcessor.postProcessed }
    }

    // ===== 테스트용 클래스 =====

    class SampleBean {
        var postProcessed = false
    }

    class SampleBeanPostProcessor : BeanPostProcessor {
        override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
            if (bean is SampleBean) {
                bean.postProcessed = true
            }
            return bean
        }
    }
}
