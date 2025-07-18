package com.galacsh.spring_context_usage.beans

import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.RootBeanDefinition
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * 빈 팩터리에 등록된 빈 정의와 빈 후처리기를 통해 의존성 주입을 수행할 수 있습니다.
 */
class BeanDependencyInjectionTest {
    private lateinit var beanFactory: DefaultListableBeanFactory

    @BeforeTest
    fun setup() {
        beanFactory = DefaultListableBeanFactory()
    }

    @Test
    fun `생성자 주입을 통해 의존성을 주입할 수 있다`() {
        // Given
        val serviceDefinition: BeanDefinition = RootBeanDefinition(SampleService::class.java)
        beanFactory.registerBeanDefinition("sampleService", serviceDefinition)

        val controllerDefinition: BeanDefinition = RootBeanDefinition(SampleController::class.java)
        beanFactory.registerBeanDefinition("sampleController", controllerDefinition)
        controllerDefinition.constructorArgumentValues.addIndexedArgumentValue(0, RuntimeBeanReference("sampleService"))

        // When
        val controller = beanFactory.getBean(SampleController::class.java)

        // Then
        assertAll(
            { assertNotNull(controller) },
            { assertNotNull(controller.sampleService) }
        )
    }

    @Test
    fun `세터 주입을 통해 의존성을 주입할 수 있다`() {
        // Given
        val serviceDefinition: BeanDefinition = RootBeanDefinition(SampleService::class.java)
        beanFactory.registerBeanDefinition("sampleService", serviceDefinition)

        val controllerDefinition: BeanDefinition = RootBeanDefinition(SampleController::class.java)
        beanFactory.registerBeanDefinition("sampleController", controllerDefinition)

        controllerDefinition.propertyValues.add("sampleService", RuntimeBeanReference("sampleService"))

        // When
        val controller = beanFactory.getBean(SampleController::class.java)

        // Then
        assertAll(
            { assertNotNull(controller) },
            { assertNotNull(controller.sampleService) }
        )
    }

    @Test
    fun `필드 주입을 통해 의존성을 주입할 수 있다`() {
        // Given
        val serviceDefinition: BeanDefinition = RootBeanDefinition(SampleService::class.java)
        beanFactory.registerBeanDefinition("sampleService", serviceDefinition)

        val controllerDefinition: BeanDefinition = RootBeanDefinition(SampleController::class.java)
        beanFactory.registerBeanDefinition("sampleController", controllerDefinition)

        val autowiredAnnotationBeanPostProcessor = AutowiredAnnotationBeanPostProcessor()
        autowiredAnnotationBeanPostProcessor.setBeanFactory(beanFactory)
        autowiredAnnotationBeanPostProcessor.setAutowiredAnnotationType(SampleAutowired::class.java)
        beanFactory.addBeanPostProcessor(autowiredAnnotationBeanPostProcessor)

        // When
        val controller = beanFactory.getBean(SampleController::class.java)

        // Then
        assertAll(
            { assertNotNull(controller) },
            { assertNotNull(controller.sampleService) }
        )
    }

    // ===== 테스트용 클래스 =====

    class SampleService

    class SampleController {

        @SampleAutowired
        lateinit var sampleService: SampleService

        @Suppress("unused")
        constructor() {
        }

        @Suppress("unused")
        constructor(sampleService: SampleService) {
            this.sampleService = sampleService
        }
    }

    @Target(AnnotationTarget.FIELD)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class SampleAutowired
}
