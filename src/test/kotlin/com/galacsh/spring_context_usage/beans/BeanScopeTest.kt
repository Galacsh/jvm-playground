package com.galacsh.spring_context_usage.beans

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.RootBeanDefinition
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

/**
 * 스프링 빈의 스코프는 빈의 생명주기를 정의합니다.
 *
 * - Singleton (기본값)
 *     - 하나의 인스턴스만 유지
 *     - 빈 팩토리에서 빈을 요청할 때마다 동일한 인스턴스 반환
 * - Prototype
 *     - 빈 팩토리에서 빈을 요청할 때마다 매번 새로운 인스턴스 생성
 */
class BeanScopeTest {
    private lateinit var beanFactory: DefaultListableBeanFactory

    @BeforeTest
    fun setup() {
        beanFactory = DefaultListableBeanFactory()
    }

    @Test
    fun `싱글톤 빈은 매번 동일한 인스턴스가 조회된다`() {
        // Given
        val beanDefinition: BeanDefinition = RootBeanDefinition(SampleBean::class.java)
        beanDefinition.scope = BeanDefinition.SCOPE_SINGLETON
        beanFactory.registerBeanDefinition("sampleBean", beanDefinition)

        // When
        val first = beanFactory.getBean(SampleBean::class.java)
        val second = beanFactory.getBean(SampleBean::class.java)

        // Then
        assertSame(first, second)
    }

    @Test
    fun `프로토타입 빈은 매번 다른 인스턴스가 조회된다`() {
        // Given
        val beanDefinition: BeanDefinition = RootBeanDefinition(SampleBean::class.java)
        beanDefinition.scope = BeanDefinition.SCOPE_PROTOTYPE
        beanFactory.registerBeanDefinition("sampleBean", beanDefinition)

        // When
        val first = beanFactory.getBean(SampleBean::class.java)
        val second = beanFactory.getBean(SampleBean::class.java)

        // Then
        assertNotSame(first, second)
    }

    // ===== 테스트용 클래스 =====

    class SampleBean
}
