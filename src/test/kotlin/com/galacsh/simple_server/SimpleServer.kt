package com.galacsh.simple_server

import java.io.Closeable

interface SimpleServer : Closeable {
    fun start()
    fun getLifecycle(): SimpleServerLifecycle
    fun getLog(): List<String>
}
