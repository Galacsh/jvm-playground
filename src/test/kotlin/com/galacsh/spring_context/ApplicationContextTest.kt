package com.galacsh.spring_context

import org.junit.jupiter.api.DisplayName
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * ApplicationContext 는 IoC 컨테이너 역할을 수행하며, 국제화(i18n), 이벤트 처리,
 * 리소스 로딩 및 환경변수 처리 등의 부가 기능을 제공합니다.
 *
 * refresh() 메서드를 호출하여 초기화하고 close() 메서드를 호출하여 종료합니다.
 *
 * - refresh()
 *     - 애플리케이션 시작 시 호출되어 설정들을 로드하거나 다시 로드하는 역할
 *     - 모든 싱글톤 빈이 인스턴스화되거나, 아니면 전혀 인스턴스화되지 않게 함
 *     - 빈 정의 로딩, 빈 팩터리 준비(후처리기 등록 등), 메시지 소스 초기화
 *     - 이벤트 퍼블리셔 초기화 및 이벤트 리스너 등록, 모든 싱글톤 인스턴스 초기화 등
 * - close()
 *     - 모든 자원과 락을 해제
 *     - 캐시된 싱글톤 객체들을 파괴
 */
class ApplicationContextTest {
    @Test
    @DisplayName("일반 클래스도 빈으로 등록할 수 있다")
    fun register_plain_class_as_bean() {
        // Given
        val context = AnnotationConfigApplicationContext(SampleImpl::class.java)

        // When
        val bean = context.getBean(Sample::class.java)

        // Then
        assertEquals("from implementation", bean.getMessage())
    }

    @Test
    @DisplayName("@Configuration 으로 빈을 등록할 수 있다")
    fun register_bean_with_configuration() {
        // Given
        val context = AnnotationConfigApplicationContext(SampleConfig::class.java)

        // When
        val bean = context.getBean(Sample::class.java)

        // Then
        assertEquals("from configuration", bean.getMessage())
    }

    @Test
    @DisplayName("@Component 로 빈을 등록할 수 있다")
    fun register_bean_with_component() {
        // Given
        val context = AnnotationConfigApplicationContext("com.galacsh.spring_context")

        // When
        val bean = context.getBean(SampleComponent::class.java)

        // Then
        assertEquals("from component", bean.getMessage())
    }

    // ===== 테스트용 클래스 =====

    interface Sample {
        fun getMessage(): String
    }

    class SampleImpl : Sample {
        override fun getMessage() = "from implementation"
    }

    @Component
    class SampleComponent : Sample {
        override fun getMessage() = "from component"
    }

    @Configuration
    open class SampleConfig {
        @Bean
        open fun sampleBeanByConfiguration() = object : Sample {
            override fun getMessage(): String = "from configuration"
        }
    }
}
