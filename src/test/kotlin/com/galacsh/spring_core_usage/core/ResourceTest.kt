package com.galacsh.spring_core_usage.core

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * spring-core는 [Resource]를 제공하여
 * 파일 시스템, 클래스패스 등 다양한 위치에서 리소스를 로드할 수 있게 합니다.
 *
 * 참고로, Resource 를 직접적으로 사용하는 것이 아닌,
 * ResourceLoader 인터페이스를 사용하면 리소스 구현체를 변경해야 할 때
 * 직접적으로 변경하지 않고 다른 리소스 로더를 주입함으로써 코드의 수정을 최소화할 수 있습니다.
 */
class ResourceTest {
    private val location = "spring_core_usage/variables.json"

    @Test
    fun `리소스 객체 직접 초기화`() {
        val classPathResource: Resource = ClassPathResource(location)
        assertTrue { classPathResource.exists() }
    }

    @Test
    fun `리소스 로더를 이용해 불러오기`() {
        val usingLoader = UsingResourceLoader(
            // 다른 리소스 로더를 주입할 수 있습니다.
            DefaultResourceLoader(javaClass.classLoader)
        )
        val resource = usingLoader.load(location)

        assertTrue { resource.exists() }
    }

    class UsingResourceLoader(private val loader: ResourceLoader) {
        fun load(location: String): Resource {
            return loader.getResource(location)
        }
    }
}
