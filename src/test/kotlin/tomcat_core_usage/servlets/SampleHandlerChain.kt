package tomcat_core_usage.servlets

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

abstract class SampleHandlerChain {
    private var nextHandler: SampleHandlerChain? = null

    protected abstract fun process(req: HttpServletRequest?, resp: HttpServletResponse?)

    fun setNext(handler: SampleHandlerChain): SampleHandlerChain {
        nextHandler = handler
        return handler
    }

    fun handle(req: HttpServletRequest?, resp: HttpServletResponse?) {
        process(req, resp)
        nextHandler?.handle(req, resp)
    }
}
