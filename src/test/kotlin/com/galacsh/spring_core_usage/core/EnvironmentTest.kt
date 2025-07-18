package com.galacsh.spring_core_usage.core

import org.junit.jupiter.api.DisplayName
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.StandardEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * spring-core는 [Environment]는 각종 프로퍼티와 프로파일을 관리하는 기능을 제공함
 */
class EnvironmentTest {
    @Test
    @DisplayName("시스템 프로퍼티, 환경 변수, 사용자 정의 프로퍼티 소스에 접근할 수 있다")
    fun access_system_properties_and_custom_properties() {
        val env: ConfigurableEnvironment = StandardEnvironment()
        env.propertySources.addLast(
            MapPropertySource(
                "My Property", mapOf(
                    "custom" to "value"
                )
            )
        )

        assertNotNull(env.systemProperties)
        assertNotNull(env.systemEnvironment)
        assertEquals("value", env.getProperty("custom"))
    }
}
