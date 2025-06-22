package support

import org.apache.catalina.Context
import org.apache.catalina.startup.Tomcat
import java.net.ServerSocket
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.io.path.createTempDirectory

object TomcatSupport {
    fun initialize(tomcat: Tomcat): Tomcat {
        Logger.getLogger("org.apache.catalina").level = Level.SEVERE

        val port = randomPort()
        val tempDir = createTempDirectory().toFile()
        val baseDir = tempDir.absolutePath

        return tomcat.apply {
            setSilent(true)
            getHost()
            setPort(port)
            setBaseDir(baseDir)

            // Tomcat 9 부터는 getConnector() 를 명시적으로 호출해야 커넥터를 생성하고 초기화함
            getConnector()
        }
    }

    fun cleanUp(tomcat: Tomcat) {
        tomcat.apply {
            stop()
            destroy()
            val baseDir = server.catalinaBase
            if (baseDir.exists()) baseDir.deleteRecursively()
        }
    }

    fun addContext(tomcat: Tomcat, basePath: String): Context {
        val docBase = tomcat.server.catalinaBase
            .resolve(basePath.removePrefix("/"))
        if (!docBase.exists()) docBase.mkdirs()

        return tomcat.addContext(basePath, docBase.absolutePath)
    }

    private fun randomPort(): Int {
        ServerSocket(0).use { socket ->
            return socket.localPort
        }
    }
}
