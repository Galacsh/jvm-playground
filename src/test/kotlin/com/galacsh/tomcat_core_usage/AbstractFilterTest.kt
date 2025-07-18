package com.galacsh.tomcat_core_usage

import com.galacsh.tomcat_core_usage.components.SampleFilter
import com.galacsh.tomcat_core_usage.components.SampleSimpleServlet
import org.junit.jupiter.api.BeforeAll
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpClient.Redirect
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Filter 는 서블릿 스펙에 정의된 기능으로,
 * 톰캣과 같은 서블릿 컨테이너 레벨에서 동작합니다.
 *
 * 따라서 비즈니스 로직과 별개인 범용 처리에 적합합니다.
 * 예를 들어, 로깅/CORS/요청-응답 변환 등을 처리할 수 있습니다.
 *
 * 반면 Interceptor 의 경우 Spring WebMVC 와 같은 프레임워크에서 제공하는 기능으로,
 * 애플리케이션 레벨에서 동작합니다.
 *
 * 따라서 비즈니스 로직에 밀접하게 연관된 처리를 수행할 때 사용됩니다.
 * 예를 들어, 비즈니스 로직과 밀접한 인증/권한 검사 등에 적합합니다.
 */
abstract class AbstractFilterTest {
    @Test
    fun `등록한 필터에 의해 변경된 요청을 받는다`() {
        // Given
        val port = SharedTomcat.port
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:$port/filter"))
            .build()

        // When
        val response = HttpClient.newBuilder()
            .followRedirects(Redirect.NORMAL)
            .build()
            .send(request, HttpResponse.BodyHandlers.ofString())

        // Then
        assertEquals("Filtered: Hello, world!", response.body())
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            SharedTomcat.addServlet("/filter", SampleSimpleServlet())
            SharedTomcat.addFilter("/filter", "*", SampleFilter())
        }
    }
}
