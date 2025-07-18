package com.galacsh.spring_context.beans

import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.support.BeanDefinitionReader
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.core.io.ClassPathResource
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * spring-beans 의 BeanDefinitionReader 를 사용하여 XML 파일로부터 빈 정의를 읽고,
 * 그 빈 정의를 바탕으로 빈을 생성할 수 있습니다.
 */
class BeanDefinitionReaderTest {

    private lateinit var beanFactory: BeanFactory
    private lateinit var definitionReader: BeanDefinitionReader

    @BeforeTest
    fun setup() {
        beanFactory = DefaultListableBeanFactory()
        definitionReader = XmlBeanDefinitionReader(beanFactory as BeanDefinitionRegistry)
    }

    @Test
    @DisplayName("XML 파일로부터 2개의 빈 정의를 읽는다")
    fun read_two_bean_definitions_from_xml() {
        // Given
        val resource = ClassPathResource("spring_context/beans/beans.xml")

        // When
        val beansCount = definitionReader.loadBeanDefinitions(resource)

        // Then
        assertEquals(2, beansCount)
    }

    @Test
    @DisplayName("읽은 빈 정의들을 토대로 빈을 생성할 수 있다")
    fun create_beans_from_read_definitions() {
        // Given
        val resource = ClassPathResource("spring_context/beans/beans.xml")
        definitionReader.loadBeanDefinitions(resource)

        // When
        val bean = beanFactory.getBean("sampleBean2", SampleBean2::class.java)

        // Then
        assertNotNull(bean)
        assertEquals("XML", bean.sampleBean1.message)
    }

    // ===== 테스트용 클래스 =====

    class SampleBean1(var message: String = "self")

    class SampleBean2(val sampleBean1: SampleBean1)
}
