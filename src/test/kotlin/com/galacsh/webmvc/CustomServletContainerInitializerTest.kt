package com.galacsh.webmvc

import com.galacsh.support.TomcatSupport
import jakarta.servlet.ServletContainerInitializer
import jakarta.servlet.ServletContext
import org.apache.catalina.startup.Tomcat
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class CustomServletContainerInitializerTest {
    private lateinit var tomcat: Tomcat

    @Test
    fun `직접 구현한 ServletContainerInitializer 를 통해 초기화할 수 있다`() {
        // Given
        val context = TomcatSupport.addContext(tomcat, "/test")
        val initializer = com.galacsh.webmvc.CustomServletContainerInitializerTest.SampleServletContainerInitializer()
        context.addServletContainerInitializer(
            initializer,
            setOf(com.galacsh.webmvc.CustomServletContainerInitializerTest.SampleAppInitializer::class.java)
        )

        // When
        tomcat.start()

        // Then
        assertContentEquals(
            listOf(
                "ServletContainerInitializer invoked",
                "AppInitializer invoked"
            ),
            com.galacsh.webmvc.CustomServletContainerInitializerTest.Log.messages()
        )
    }

    @BeforeTest
    fun setUp() {
        tomcat = TomcatSupport.initialize(Tomcat())
    }

    @AfterTest
    fun tearDown() {
        TomcatSupport.cleanUp(tomcat)
        com.galacsh.webmvc.CustomServletContainerInitializerTest.Log.clear()
    }

    object Log {
        private val messages = mutableListOf<String>()

        fun add(message: String) {
            com.galacsh.webmvc.CustomServletContainerInitializerTest.Log.messages.add(message)
        }

        fun clear() {
            com.galacsh.webmvc.CustomServletContainerInitializerTest.Log.messages.clear()
        }

        fun messages(): List<String> = com.galacsh.webmvc.CustomServletContainerInitializerTest.Log.messages
    }

    class SampleServletContainerInitializer : ServletContainerInitializer {
        override fun onStartup(interestedClasses: MutableSet<Class<*>>?, servletContext: ServletContext?) {
            com.galacsh.webmvc.CustomServletContainerInitializerTest.Log.add("ServletContainerInitializer invoked")
            interestedClasses?.forEach {
                if (com.galacsh.webmvc.CustomServletContainerInitializerTest.AppInitializer::class.java.isAssignableFrom(
                        it
                    )
                ) {
                    val initializer = it.getDeclaredConstructor()
                        .newInstance() as com.galacsh.webmvc.CustomServletContainerInitializerTest.AppInitializer

                    if (servletContext != null) {
                        initializer.onStartup(servletContext)
                    }
                }
            }
        }
    }

    class SampleAppInitializer : com.galacsh.webmvc.CustomServletContainerInitializerTest.AppInitializer {
        override fun onStartup(servletContext: ServletContext) {
            com.galacsh.webmvc.CustomServletContainerInitializerTest.Log.add("AppInitializer invoked")
        }
    }

    interface AppInitializer {
        fun onStartup(servletContext: ServletContext)
    }
}
