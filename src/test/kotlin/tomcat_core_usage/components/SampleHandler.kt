package tomcat_core_usage.components

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

interface SampleHandler {
    fun handle(req: HttpServletRequest?, resp: HttpServletResponse?)
}
