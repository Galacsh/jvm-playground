package tomcat_core_usage

import jakarta.servlet.Servlet
import org.apache.catalina.startup.Tomcat
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

        instance.addContext(basePath, docBase.absolutePath)
        instance.addServlet(basePath, servlet.javaClass.name, servlet)
            .apply { addMapping("/") }
    }

    private fun randomPort(): Int {
        ServerSocket(0).use { socket ->
            return socket.localPort
        }
    }
}
