package spring_context_usage

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.context.MessageSource
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.core.io.Resource
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageSourceTest {
    @Test
    fun `메시지 소스를 사용하여 국제화된 메시지를 가져올 수 있다`() {
        // Given
        val username = "John"
        val context = AnnotationConfigApplicationContext(YamlMessageSourceConfig::class.java)

        // When - Korea locale
        val koreanMessage = context.getMessage("greeting", arrayOf(username), Locale.KOREA)

        // Then
        assertEquals("안녕하세요, $username!", koreanMessage)

        // When - English locale
        val englishMessage = context.getMessage("greeting", arrayOf(username), Locale.ENGLISH)

        // Then
        assertEquals("Hello, $username!", englishMessage)
    }

    @Configuration
    open class YamlMessageSourceConfig {
        /**
         * 기본 MessageSource 로 사용되기 위해서는 빈 이름이 "messageSource"여야 합니다.
         */
        @Bean
        open fun messageSource(): MessageSource {
            val source = object : ReloadableResourceBundleMessageSource() {
                override fun loadProperties(resource: Resource, filename: String): Properties {
                    return resource.inputStream.use {
                        // YAML 파일 처리
                        if (filename.endsWith(".yaml") || filename.endsWith(".yml")) {
                            val yaml = YamlPropertiesFactoryBean()
                            yaml.setResources(resource)
                            yaml.getObject() ?: throw RuntimeException("asdfQBR")
                        }
                        // Fallback
                        else {
                            super.loadProperties(resource, filename)
                        }
                    }
                }
            }

            source.apply {
                setBasename("classpath:spring_context_usage/messages")
                setFileExtensions(listOf(".yaml", ".yml"))
                setDefaultLocale(Locale.KOREA)
                setDefaultEncoding("UTF-8")
                setCacheSeconds(0)
            }

            return source
        }
    }
}
