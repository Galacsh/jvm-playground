package com.galacsh.tomcat_core_usage

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
 * Tomcat 서버 관련 용어:
 *
 * - Catalina
 *     - Servlet 들의 컨테이너 역할을 하는 Tomcat 의 핵심 컴포넌트
 *     - 서블릿, JSP, 웹 애플리케이션(Context)의 생성, 초기화(init), 요청 처리(service), 소멸(destroy) 등 전체 생명주기 관리
 *     - 웹 애플리케이션의 배포 및 실행 환경을 제공
 *     - 보안, 세션 관리, 로깅 등의 다양한 컨테이너 서비스를 담당
 * - Connector (= Coyote)
 *     - 클라이언트(웹 브라우저)로부터 요청을 받아들이고 응답을 돌려주는 통로
 * - Webapp
 *     - 동적인 웹 콘텐츠(서블릿, JSP 등)와 정적 콘텐츠(HTML, CSS, JavaScript, 이미지 등)의 묶음
 *     - WAR(Web Archive) 파일 형태로 배포될 수 있음
 * - Context
 *     - Tomcat 입장에서 Webapp 을 지칭하는 용어라고 할 수 있음
 *     - 각각 고유한 context path 를 가짐
 *     - 웹 애플리케이션의 설정(web.xml), 클래스 파일, JSP 파일, 정적 자원 등을 포함함
 * - Servlet
 *     - 웹 서버 내에서 실행되는 작은 자바 프로그램
 *     - HTTP 요청을 처리하고 응답을 생성하는 역할
 * - HttpServlet
 *     - HTTP 프로토콜에 특화된 서블릿
 *     - doGet, doPost 등의 메서드를 오버라이드하여 HTTP 요청을 처리
 *
 * 클라이언트가 Connector를 통해 웹 서버에 요청을 보내면,
 * 이 요청은 context path 에 따라 특정 Context로 전달됩니다.
 *
 * 이 Context는 서버의 DocBase에 위치한 실제 파일들을 참조하며,
 * 요청된 URL 패턴에 따라 Context 내부에 정의된
 * 적절한 Servlet (대부분은 HttpServlet)이 호출됩니다.
 *
 * Servlet은 어 요청을 처리하고 응답을 생성하여
 * 다시 Connector를 통해 클라이언트에게 전달하는 방식으로 동작합니다.
 *
 * 경로 관련 설명:
 *
 * - baseDir
 *     - 최상위 기본 디렉토리
 *     - 임시 파일, 로그 파일, 설정 파일 등을 저장하는 데 사용
 * - appBase
 *     - Host에 자동으로 배포될 웹 애플리케이션(컨텍스트)들이 위치
 *     - .war 파일이나 압축이 풀린(exploded) 웹 애플리케이션 디렉토리를 넣어두는 곳
 *     - `autoDeploy = true`일 경우 Tomcat 이 자동으로 해당 웹 애플리케이션을 감지하고 배포
 * - docBase
 *     - 해당 웹 애플리케이션의 실제 파일들이 저장된 물리적인 디렉토리 또는 WAR 파일의 경로
 *     - 상대 경로인 경우 appBase/docBase 로 해석됨
 *     - 절대 경로인 경우 appBase 와는 무관
 *     - contextPath 와 매핑되어 요청을 처리하는 서블릿이 위치하는 곳
 */
abstract class AbstractTomcatBasicTest {
    @Test
    fun `Tomcat 서버를 실행하고 등록한 서블릿이 정상적으로 응답한다`() {
        // Given
        val port = SharedTomcat.port
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:$port/hello"))
            .build()

        // When
        val response = HttpClient.newBuilder()
            .followRedirects(Redirect.NORMAL)
            .build()
            .send(request, HttpResponse.BodyHandlers.ofString())

        // Then
        assertEquals("Hello, world!", response.body())
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            SharedTomcat.addServlet(
                basePath = "/hello",
                servlet = SampleSimpleServlet()
            )
        }
    }
}
