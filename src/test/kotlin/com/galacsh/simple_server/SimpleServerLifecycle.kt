package com.galacsh.simple_server

interface SimpleServerLifecycle {
    fun signalReady()
    fun awaitReady()
    fun shouldStop(): Boolean
    fun signalStop()
    fun signalStopped()
    fun awaitStopped()
}
