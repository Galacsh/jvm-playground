package com.galacsh.simple_server

import com.galacsh.support.PortUtils
import org.junit.jupiter.api.DisplayName
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.nio.charset.Charset
import kotlin.test.Test
import kotlin.test.assertContentEquals

class SimpleServerTest {
    @Test
    @DisplayName("간단한 서버를 실행하고 클라이언트의 메시지를 수신하는지 테스트")
    fun simple_server_listen_and_receive_message() {
        // Given
        val address = InetSocketAddress(PortUtils.randomPort())
        val server = SimpleServerImpl(address)
        val lifecycle = server.getLifecycle()

        Thread { // 별도의 스레드에서 서버 실행
            server.use { it.start() }
        }.start()

        lifecycle.awaitReady() // 서버가 준비될 때까지 대기

        // When
        sendMessageFromClient(address, "Hello, Server!")
        lifecycle.signalStop()
        lifecycle.awaitStopped()

        // Then
        assertContentEquals(
            expected = listOf(
                "서버 시작됨",
                "클라이언트 메시지 수신: Hello, Server!",
                "서버 종료됨"
            ),
            actual = server.getLog()
        )
    }

    private fun sendMessageFromClient(address: SocketAddress, message: String) {
        SocketChannel.open(address).use { clientChannel ->
            val buffer = ByteBuffer.wrap(message.toByteArray(Charset.defaultCharset()))
            clientChannel.write(buffer)
            clientChannel.shutdownOutput()
        }
    }
}
