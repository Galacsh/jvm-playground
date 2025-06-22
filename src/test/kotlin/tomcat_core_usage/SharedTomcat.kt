package tomcat_core_usage

import jakarta.servlet.Filter
import jakarta.servlet.Servlet
import org.apache.catalina.core.StandardContext
import org.apache.catalina.startup.Tomcat
import org.apache.tomcat.util.descriptor.web.FilterDef
import org.apache.tomcat.util.descriptor.web.FilterMap
import support.TomcatSupport

object SharedTomcat {
    private lateinit var instance: Tomcat
    val port
        get() = instance.connector.port

    fun initialize() {
        if (::instance.isInitialized) return

        instance = TomcatSupport.initialize(Tomcat())
        instance.start()
    }

    fun cleanUp() {
        TomcatSupport.cleanUp(instance)
    }

    fun addServlet(basePath: String, servlet: Servlet) {
        val docBase = instance.server.catalinaBase
            .resolve(basePath.removePrefix("/"))
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
}
