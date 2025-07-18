package com.galacsh.tomcat_core

import com.galacsh.tomcat_core.components.SampleDispatcherServlet
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpClient.Redirect
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * DispatcherServlet 은 HttpServlet 을 상속받아 서블릿 컨테이너(e.g. Tomcat)에 등록되며
 * 프런트 컨트롤러(Front Controller) 패턴을 구현한 Servlet 입니다.
 *
 * 즉, 모든 HTTP 요청을 중앙에서 처리하는 역할을 담당하며
 * 실제 작업은 다양한 컴포넌트들에 위임하여 처리합니다.
 *
 * 아래 테스트와는 무관하게, DispatcherServlet 의 주요 작업은 다음과 같습니다.
 *
 * - 서블릿 컨테이너 통해 클라이언트로부터의 HTTP 요청 수신
 * - WebApplicationContext를 찾아서 요청의 속성으로 바인딩
 * - Multipart 요청일 경우, MultipartHttpServletRequest로 래핑된 request 로 변환
 * - 적절한 핸들러 실행 체인을 찾아 핸들러 실행
 *     - 핸들러 실행 체인은 인터셉터를 포함할 수 있음
 *     - 인터셉터는 핸들러 실행 전후에 추가 작업 수행
 * - 예외 처리
 * - ViewResolver 통해 뷰 렌더링
 * - HTTP 응답 반환
 */
abstract class AbstractExampleDispatcherServletTest {
    @Test
    @DisplayName("one 경로에 맞는 핸들러로 요청을 전달하여 응답을 생성한다")
    fun request_returns_first_handler_response() {
        // Given
        val port = SharedTomcat.port
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:$port/dispatcher/one"))
            .build()

        // When
        val response = HttpClient.newBuilder()
            .followRedirects(Redirect.NORMAL)
            .build()
            .send(request, HttpResponse.BodyHandlers.ofString())

        // Then
        assertEquals("first", response.body())
    }

    @Test
    @DisplayName("two 경로에 맞는 핸들러로 요청을 전달하여 응답을 생성한다")
    fun request_returns_first_and_second_handler_response() {
        // Given
        val port = SharedTomcat.port
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:$port/dispatcher/two"))
            .build()

        // When
        val response = HttpClient.newBuilder()
            .followRedirects(Redirect.NORMAL)
            .build()
            .send(request, HttpResponse.BodyHandlers.ofString())

        // Then
        assertEquals("first/second", response.body())
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            val basePath = "/dispatcher"
            SharedTomcat.addServlet(
                basePath,
                SampleDispatcherServlet(basePath)
            )
        }
    }
}
