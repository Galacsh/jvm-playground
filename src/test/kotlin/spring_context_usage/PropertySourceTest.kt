package spring_context_usage

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import kotlin.test.Test
import kotlin.test.assertEquals

class PropertySourceTest {
    @Test
    fun `프로퍼티 소스를 사용하여 설정 파일을 로드할 수 있다`() {
        // Given
        val context = AnnotationConfigApplicationContext(SamplePropertiesConfig::class.java)

        // When
        val name = context.environment.getProperty(
            "sample.name",
            "Couldn't load property"
        )

        // Then
        assertEquals("John", name)
    }

    // ===== 테스트용 클래스 =====

    @Configuration
    @PropertySource("classpath:spring_context_usage/sample.properties")
    open class SamplePropertiesConfig
}
