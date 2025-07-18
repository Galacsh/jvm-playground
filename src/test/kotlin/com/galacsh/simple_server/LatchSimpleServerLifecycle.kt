package com.galacsh.simple_server

import java.util.concurrent.CountDownLatch

class LatchSimpleServerLifecycle : SimpleServerLifecycle {
    private val readyLatch = CountDownLatch(1)
    private val stopLatch = CountDownLatch(1)
    private val stoppedLatch = CountDownLatch(1)

    override fun signalReady() = readyLatch.countDown()
    override fun awaitReady() = readyLatch.await()
    override fun shouldStop(): Boolean = stopLatch.count == 0L
    override fun signalStop() = stopLatch.countDown()
    override fun signalStopped() = stoppedLatch.countDown()
    override fun awaitStopped() = stoppedLatch.await()
}
