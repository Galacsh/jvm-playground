package com.galacsh.simple_server

import java.io.Closeable
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.charset.Charset

class SimpleServerImpl(private val socketAddress: SocketAddress) : SimpleServer {
    private lateinit var selector: Selector
    private lateinit var serverChannel: ServerSocketChannel
    private val lifecycle: SimpleServerLifecycle = LatchSimpleServerLifecycle()
    private val log: MutableList<String> = mutableListOf()

    override fun start() {
        // 서버 기본 SelectorProvider를 사용하여 Selector 생성
        selector = Selector.open()
        serverChannel = ServerSocketChannel.open().apply {
            // 논블로킹 모드 설정
            configureBlocking(false)
            // 지정된 주소에 바인딩
            bind(socketAddress)
            // Selector에 Accept 이벤트 등록
            register(selector, SelectionKey.OP_ACCEPT)
        }

        log.add("서버 시작됨")
        lifecycle.signalReady()

        while (true) {
            // 블로킹 모드로 I/O 이벤트 대기
            selector.select(100)
            // 신호가 있는 키들을 가져옴
            val keys = selector.selectedKeys().iterator()

            while (keys.hasNext()) {
                val key = keys.next()
                keys.remove()

                when {
                    key.isAcceptable -> {
                        serverChannel.accept().apply {
                            // 클라이언트 채널을 논블로킹 모드로 설정
                            configureBlocking(false)
                            // Selector에 Read 이벤트 등록
                            register(selector, SelectionKey.OP_READ)
                        }
                    }

                    key.isReadable -> {
                        val clientChannel = key.channel() as SocketChannel
                        try {
                            val buffer = ByteBuffer.allocate(1024)
                            val bytesRead = clientChannel.read(buffer)

                            // 클라이언트 연결 종료됨
                            if (bytesRead == -1) {
                                safeClose(clientChannel)
                            }
                            // 클라이언트로부터 메시지를 받음
                            else {
                                buffer.flip()
                                val message = Charset.defaultCharset().decode(buffer).toString()
                                log.add("클라이언트 메시지 수신: $message")
                                buffer.clear()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            safeClose(clientChannel)
                        }
                    }
                }
            }

            // 서버 종료 신호가 있었다면 반복문 종료
            if (lifecycle.shouldStop()) break
        }
    }

    override fun getLifecycle() = lifecycle

    override fun getLog() = log

    override fun close() {
        safeClose(selector)
        safeClose(serverChannel)
        log.add("서버 종료됨")
        lifecycle.signalStopped()
    }

    private fun safeClose(closeable: Closeable) {
        try {
            closeable.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
