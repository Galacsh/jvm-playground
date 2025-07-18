package com.galacsh.tomcat_core.components

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class SampleHandlerChain(private var handler: SampleHandler) {
    private var interceptorList = mutableListOf<SampleHandlerInterceptor>()

    fun handle(req: HttpServletRequest?, resp: HttpServletResponse?) {
        applyPreHandle(req, resp)
        handler.handle(req, resp)
        applyPostHandle(req, resp)
    }

    private fun applyPreHandle(req: HttpServletRequest?, resp: HttpServletResponse?) {
        interceptorList.forEach { it.preHandle(req, resp) }
    }

    private fun applyPostHandle(req: HttpServletRequest?, resp: HttpServletResponse?) {
        interceptorList.forEach { it.postHandle(req, resp) }
    }

    fun addInterceptor(interceptor: SampleHandlerInterceptor) {
        interceptorList.add(interceptor)
    }
}
