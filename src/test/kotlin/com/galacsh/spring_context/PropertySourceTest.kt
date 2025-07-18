package com.galacsh.spring_context

import org.junit.jupiter.api.DisplayName
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import kotlin.test.Test
import kotlin.test.assertEquals

class PropertySourceTest {
    @Test
    @DisplayName("프로퍼티 소스를 사용하여 설정 파일을 로드할 수 있다")
    fun load_property_file_with_property_source() {
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
    @PropertySource("classpath:spring_context/sample.properties")
    open class SamplePropertiesConfig
}
