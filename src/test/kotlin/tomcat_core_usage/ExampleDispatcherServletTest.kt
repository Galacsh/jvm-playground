package tomcat_core_usage

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.catalina.startup.Tomcat
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
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
 * - 적절한 핸들러를 찾아 핸들러 실행
 * - 예외 처리
 * - ViewResolver 통해 뷰 렌더링
 * - HTTP 응답 반환
 */
class ExampleDispatcherServletTest {
    private val port = 8080
    private val contextPath = ""
    private lateinit var docBaseDir: File
    private lateinit var docBase: String
    private lateinit var tomcat: Tomcat

    @Test
    fun `one 경로에 맞는 핸들러로 요청을 전달하여 응답을 생성한다`() {
        // Given
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:$port/one"))
            .build()

        // When
        val response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString())

        // Then
        assertEquals("first", response.body())
    }

    @Test
    fun `two 경로에 맞는 핸들러로 요청을 전달하여 응답을 생성한다`() {
        // Given
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:$port/two"))
            .build()

        // When
        val response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString())

        // Then
        assertEquals("first/second", response.body())
    }

    // ===== 톰캣 초기화 및 정리 =====

    @BeforeTest
    fun setup() {
        Logger.getLogger("org.apache.catalina").level = Level.SEVERE

        docBaseDir = createTempDirectory("tomcat_dispatcher_servlet_test").toFile().apply { mkdirs() }
        docBase = docBaseDir.absolutePath

        tomcat = Tomcat().apply {
            setPort(port)
            setSilent(true)
            addContext(contextPath, docBase)
            addServlet(contextPath, "DispatcherServlet", SampleDispatcherServlet())
                .apply { addMapping("/") }

            // Tomcat 9 부터는 getConnector() 를 명시적으로 호출해야 커넥터를 생성하고 초기화함
            getConnector()
            start()
        }
    }

    @AfterTest
    fun tearDown() {
        tomcat.stop()
        tomcat.destroy()
        docBaseDir.deleteRecursively()
    }

    // ===== 테스트용 클래스 =====

    abstract class HandlerChain {
        private var nextHandler: HandlerChain? = null

        protected abstract fun process(req: HttpServletRequest?, resp: HttpServletResponse?)

        fun setNext(handler: HandlerChain): HandlerChain {
            nextHandler = handler
            return handler
        }

        fun handle(req: HttpServletRequest?, resp: HttpServletResponse?) {
            process(req, resp)
            nextHandler?.handle(req, resp)
        }
    }

    class SampleDispatcherServlet : HttpServlet() {
        companion object {
            private val HTTP_SERVLET_METHODS = setOf(
                "DELETE", "HEAD", "GET", "OPTIONS", "POST", "PUT", "TRACE"
            )

            private val mappingHandlers = mapOf(
                "/one" to object : HandlerChain() {
                    override fun process(req: HttpServletRequest?, resp: HttpServletResponse?) {
                        resp?.writer?.print("first")
                    }
                },
                "/two" to object : HandlerChain() {
                    override fun process(req: HttpServletRequest?, resp: HttpServletResponse?) {
                        resp?.writer?.print("first")
                    }
                }.apply {
                    setNext(object : HandlerChain() {
                        override fun process(req: HttpServletRequest?, resp: HttpServletResponse?) {
                            resp?.writer?.print("/second")
                        }
                    })
                },
            )
        }

        private fun processRequest(req: HttpServletRequest?, resp: HttpServletResponse?) {
            val handler = getHandler(req)
            if (handler == null) noHandlerFound(req, resp)
            else handler.handle(req, resp)
        }

        private fun getHandler(req: HttpServletRequest?): HandlerChain? {
            val uri = req?.requestURI ?: throw RuntimeException("Request URI is null")

            for ((path, handler) in mappingHandlers) {
                if (uri.startsWith(path)) {
                    return handler
                }
            }

            return null
        }

        private fun noHandlerFound(req: HttpServletRequest?, resp: HttpServletResponse?) {
            resp?.sendError(HttpServletResponse.SC_NOT_FOUND, "No handler found")
        }

        override fun service(req: HttpServletRequest?, resp: HttpServletResponse?) {
            if (HTTP_SERVLET_METHODS.contains(req?.method)) {
                super.service(req, resp)
            } else {
                processRequest(req, resp)
            }
        }

        override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
            processRequest(req, resp)
        }

        override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
            processRequest(req, resp)
        }

        override fun doPut(req: HttpServletRequest?, resp: HttpServletResponse?) {
            processRequest(req, resp)
        }

        override fun doDelete(req: HttpServletRequest?, resp: HttpServletResponse?) {
            processRequest(req, resp)
        }
    }
}
