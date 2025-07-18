package com.galacsh.support

import java.net.ServerSocket

object PortUtils {
    fun randomPort(): Int {
        ServerSocket(0).use { socket ->
            return socket.localPort
        }
    }
}
