package com.galacsh.tomcat_core.components

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class SampleDispatcherServlet(private val basePath: String = "") : HttpServlet() {
    companion object {
        private val HTTP_SERVLET_METHODS = setOf(
            "DELETE", "HEAD", "GET", "OPTIONS", "POST", "PUT", "TRACE"
        )

        private val oneHandlerChain = SampleHandlerChain(
            object : SampleHandler {
                override fun handle(req: HttpServletRequest?, resp: HttpServletResponse?) {
                    resp?.writer?.print("first")
                }
            }
        )

        private val twoHandlerChain = SampleHandlerChain(
            object : SampleHandler {
                override fun handle(req: HttpServletRequest?, resp: HttpServletResponse?) {
                    resp?.writer?.print("first")
                }
            }
        ).apply {
            addInterceptor(object : SampleHandlerInterceptor {
                override fun postHandle(req: HttpServletRequest?, resp: HttpServletResponse?) {
                    resp?.writer?.print("/second")
                }
            })
        }

        private val mappingHandlers = mapOf(
            "/one" to oneHandlerChain,
            "/two" to twoHandlerChain,
        )
    }

    private fun processRequest(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val handler = getHandler(req)
        if (handler == null) noHandlerFound(req, resp)
        else handler.handle(req, resp)
    }

    private fun getHandler(req: HttpServletRequest?): SampleHandlerChain? {
        var uri = req?.requestURI ?: throw RuntimeException("Request URI is null")
        uri = uri.removePrefix(basePath)

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
