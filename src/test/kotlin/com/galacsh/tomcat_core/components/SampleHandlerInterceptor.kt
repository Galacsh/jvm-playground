package com.galacsh.tomcat_core.components

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

interface SampleHandlerInterceptor {
    fun preHandle(req: HttpServletRequest?, resp: HttpServletResponse?) {
        // Default implementation does nothing
    }

    fun postHandle(req: HttpServletRequest?, resp: HttpServletResponse?) {
        // Default implementation does nothing
    }
}
