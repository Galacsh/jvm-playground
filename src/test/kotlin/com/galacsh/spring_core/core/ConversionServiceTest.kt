package com.galacsh.spring_core.core

import org.junit.jupiter.api.DisplayName
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.support.ConfigurableConversionService
import org.springframework.core.env.StandardEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * spring-core는 [ConversionService]를 제공하여 타입 변환을 지원합니다.
 * 이때, 타입 변환기를 직접 등록할 수도 있습니다.
 */
class ConversionServiceTest {
    @Test
    @DisplayName("기본 converters")
    fun default_converters() {
        // 💡 기본 ConversionService: StandardEnvironment 통해 사용 가능
        val conversionService: ConversionService = StandardEnvironment().conversionService

        // 기본적으로 제공되는 타입 변환기 사용
        assertTrue { conversionService.canConvert(String::class.java, Int::class.java) }
        assertEquals("42", conversionService.convert(42, String::class.java))
    }

    @Test
    @DisplayName("converter 직접 등록하는 것도 가능")
    fun register_custom_converter() {
        val conversionService: ConversionService = StandardEnvironment().conversionService

        // 직접 등록한 타입 변환기 시연을 위한 샘플 클래스
        data class Sample(val value: Int)

        // 직접 등록한 타입 변환기 사용
        val configurable = conversionService as ConfigurableConversionService
        configurable.addConverter(Sample::class.java, String::class.java) { it.value.toString() }

        assertTrue { configurable.canConvert(Sample::class.java, String::class.java) }
        assertEquals("123", configurable.convert(Sample(123), String::class.java))
    }
}
