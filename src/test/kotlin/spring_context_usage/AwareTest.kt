package spring_context_usage

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Aware 인터페이스를 구현함으로써 Spring의 다양한 객체에 접근할 수 있습니다.
 *
 * 예를 들어, ApplicationContextAware 인터페이스를 구현하면
 * ApplicationContext 객체에 접근할 수 있습니다.
 *
 * 아래 예제에서는 ApplicationContextAware를 구현하여
 * 접근 가능해진 ApplicationContext를 전역적으로 접근할 수 있도록 하여
 * 빈이 아니더라도 다른 빈에 접근할 수 있는 방법을 보여줍니다.
 */
class AwareTest {
    @Test
    fun `Aware 인터페이스를 사용하여 원하는 Spring 객체에 접근할 수 있다`() {
        // Given
        AnnotationConfigApplicationContext(
            ApplicationContextPortal::class.java,
            SampleBean::class.java
        )

        // When
        val bean = ApplicationContextPortal
            .applicationContext
            .getBean(SampleBean::class.java)

        // Then
        assertEquals("hello", bean.message)
    }

    // ===== 테스트용 클래스 =====

    class SampleBean(val message: String = "hello")

    object ApplicationContextPortal : ApplicationContextAware {
        lateinit var applicationContext: ApplicationContext
            private set

        override fun setApplicationContext(applicationContext: ApplicationContext) {
            this.applicationContext = applicationContext
        }
    }
}
