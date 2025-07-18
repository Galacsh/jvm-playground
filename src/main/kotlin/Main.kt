package com.galacsh

import org.springframework.core.SpringVersion

fun main() {
    val message = """
        
        ==========================================
        Spring Framework Version: ${SpringVersion.getVersion() ?: "Unknown"}
        Run tests with:
            ./gradlew test
        ==========================================
        
    """.trimIndent()

    println(message)
}
