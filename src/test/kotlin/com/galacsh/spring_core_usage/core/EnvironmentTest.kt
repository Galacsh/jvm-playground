package com.galacsh.spring_core_usage.core

import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.StandardEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * spring-core는 [Environment]를 제공하여 다음을 관리하는 기능을 포함합니다.
 *
 * - 시스템 환경 변수
 * - 시스템 프로퍼티
 * - 사용자 정의 프로퍼티 소스
 */
class EnvironmentTest {
    @Test
    fun `access system properties, environment variables and manage custom properties`() {
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
