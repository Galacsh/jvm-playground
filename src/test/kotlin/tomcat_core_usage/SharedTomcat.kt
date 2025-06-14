package tomcat_core_usage

import jakarta.servlet.Filter
import jakarta.servlet.Servlet
import org.apache.catalina.core.StandardContext
import org.apache.catalina.startup.Tomcat
import org.apache.tomcat.util.descriptor.web.FilterDef
import org.apache.tomcat.util.descriptor.web.FilterMap
import java.io.File
import java.net.ServerSocket
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.io.path.createTempDirectory

object SharedTomcat {
    private lateinit var instance: Tomcat
    private lateinit var tempDir: File
    var port = 0; private set

    fun initialize() {
        if (::instance.isInitialized) return

        Logger.getLogger("org.apache.catalina").level = Level.SEVERE

        port = randomPort()
        tempDir = createTempDirectory("tomcat_test").toFile()
        val baseDir = tempDir.absolutePath

        instance = Tomcat().apply {
            setSilent(true)
            getHost()
            setPort(port)
            setBaseDir(baseDir)

            // Tomcat 9 부터는 getConnector() 를 명시적으로 호출해야 커넥터를 생성하고 초기화함
            getConnector()
            start()
        }
    }

    fun cleanUp() {
        instance.stop()
        instance.destroy()
        if (tempDir.exists()) tempDir.deleteRecursively()
    }

    fun addServlet(basePath: String, servlet: Servlet) {
        val docBase = tempDir.resolve(basePath.removePrefix("/"))
        if (!docBase.exists()) docBase.mkdirs()

        val servletName = "${basePath.removePrefix("/")}-${servlet::class.simpleName}"

        instance.addContext(basePath, docBase.absolutePath)
        instance.addServlet(basePath, servletName, servlet)
            .apply { addMapping("/") }
    }

    fun addFilter(basePath: String, urlPattern: String, filter: Filter) {
        val context = instance.host.findChild(basePath)
        if (context !is StandardContext) {
            throw IllegalArgumentException("Context not found for base path: $basePath")
        }

        val filterName = "${basePath.removePrefix("/")}-${filter::class.simpleName}"

        val filterDef = FilterDef().apply {
            setFilterName(filterName)
            setFilter(filter)
        }
        context.addFilterDef(filterDef)

        val filterMap = FilterMap().apply {
            setFilterName(filterName)
            addURLPattern(urlPattern)
        }
        context.addFilterMap(filterMap)

        // 이미 시작된 컨텍스트에 필터를 추가한 경우, 필터를 다시 시작해야 함
        context.filterStop()
        context.filterStart()
    }

    private fun randomPort(): Int {
        ServerSocket(0).use { socket ->
            return socket.localPort
        }
    }
}
