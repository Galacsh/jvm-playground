package spring_beans_usage

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.beans.factory.support.RootBeanDefinition
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * 빈 정의를 등록하여 원하는 빈을 생성할 수 있습니다.
 * 빈 정의를 등록하는 방법에는 다음과 같은 방법이 있습니다.
 *
 * - RootBeanDefinition
 *     - 런타임 시에 빈 인스턴스를 생성하는 데 필요한 모든 정보를 가지는 정의
 * - GenericBeanDefinition
 *     - 일반적인 빈 정의로, 모든 빈 메타데이터를 포함할 수 있지만 유연성을 제공
 *     - 주로 프로그래밍 방식으로 빈 정의를 생성하거나, 부모/자식 설정할 때 사용
 * - BeanDefinitionBuilder
 *     - BeanDefinition 객체를 쉽고 간편하게 생성하고 설정할 수 있도록 돕는 빌더 패턴
 * - Factory Method (static, instance)
 *     - static: 정적 팩터리 메서드를 통해 빈을 생성
 *     - instance: 팩터리 빈을 등록하고, 해당 빈의 메서드를 통해 빈을 생성
 */
class BeanDefinitionTest {
    private lateinit var beanFactory: DefaultListableBeanFactory

    @BeforeTest
    fun setup() {
        beanFactory = DefaultListableBeanFactory()
    }

    @Test
    fun `빈 등록 및 조회 - RootBeanDefinition`() {
        // Given
        val beanDefinition: BeanDefinition = RootBeanDefinition(SampleParentBean::class.java)
        beanFactory.registerBeanDefinition("rootBean", beanDefinition)

        // When
        val bean = this.beanFactory.getBean(SampleParentBean::class.java)

        // Then
        assertNotNull(bean)
    }

    @Test
    fun `빈 등록 및 조회 - GenericBeanDefinition`() {
        // Given
        val parentDefinition = GenericBeanDefinition()
        parentDefinition.beanClass = SampleParentBean::class.java
        parentDefinition.propertyValues.add("message", "from parent")
        beanFactory.registerBeanDefinition("parentBean", parentDefinition)

        val childDefinition = GenericBeanDefinition()
        childDefinition.beanClass = SampleChildBean::class.java
        childDefinition.parentName = "parentBean"
        beanFactory.registerBeanDefinition("childBean", childDefinition)

        // When
        val bean = this.beanFactory.getBean(SampleChildBean::class.java) // 이때 Bean이 생성됨

        // Then
        assertNotNull(bean)
        assertEquals("from parent", bean.message)
    }

    @Test
    fun `BeanDefinitionBuilder 를 사용하여 손쉽게 빈 정의를 작성할 수 있다`() {
        // Given
        val beanDefinition = BeanDefinitionBuilder
            .genericBeanDefinition(SampleParentBean::class.java)
            .setScope(BeanDefinition.SCOPE_SINGLETON)
            .beanDefinition
        beanFactory.registerBeanDefinition("beanFromBuilder", beanDefinition)

        // When
        val bean = beanFactory.getBean(SampleParentBean::class.java)

        // Then
        assertNotNull(bean)
    }

    @Test
    fun `정적 팩터리 메서드로 빈을 생성할 수 있다`() {
        // Given
        val sampleBeanFactory = RootBeanDefinition(SampleBeanFactory::class.java)
        sampleBeanFactory.factoryMethodName = "staticCreate"
        beanFactory.registerBeanDefinition("sampleBean", sampleBeanFactory)

        // When
        val bean = beanFactory.getBean("sampleBean") as SampleParentBean

        // Then
        assertNotNull(bean)
        assertEquals("static factory method", bean.message)
    }

    @Test
    fun `인스턴스 팩터리 메서드로 빈을 생성할 수 있다`() {
        // Given
        val sampleBeanFactory = RootBeanDefinition(SampleBeanFactory::class.java)
        beanFactory.registerBeanDefinition("sampleBeanFactory", sampleBeanFactory)

        val parentDefinition = RootBeanDefinition(SampleParentBean::class.java).apply {
            factoryBeanName = "sampleBeanFactory"
            factoryMethodName = "create"
        }
        beanFactory.registerBeanDefinition("parentBean", parentDefinition)

        // When
        val bean = beanFactory.getBean("parentBean") as SampleParentBean

        // Then
        assertNotNull(bean)
        assertEquals("instance factory method", bean.message)
    }

    // ===== 테스트용 클래스 =====

    open class SampleParentBean {
        lateinit var message: String
    }

    class SampleChildBean : SampleParentBean()

    class SampleBeanFactory {
        companion object {
            @JvmStatic
            @Suppress("unused")
            fun staticCreate(): SampleParentBean = SampleParentBean()
                .apply { message = "static factory method" }
        }

        @Suppress("unused")
        fun create(): SampleParentBean = SampleParentBean()
            .apply { message = "instance factory method" }
    }
}
