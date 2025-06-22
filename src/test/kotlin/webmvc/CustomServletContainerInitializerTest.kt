package webmvc

import jakarta.servlet.ServletContainerInitializer
import jakarta.servlet.ServletContext
import org.apache.catalina.startup.Tomcat
import support.TomcatSupport
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
        val initializer = SampleServletContainerInitializer()
        context.addServletContainerInitializer(
            initializer,
            setOf(SampleAppInitializer::class.java)
        )

        // When
        tomcat.start()

        // Then
        assertContentEquals(
            listOf(
                "ServletContainerInitializer invoked",
                "AppInitializer invoked"
            ),
            Log.messages()
        )
    }

    @BeforeTest
    fun setUp() {
        tomcat = TomcatSupport.initialize(Tomcat())
    }

    @AfterTest
    fun tearDown() {
        TomcatSupport.cleanUp(tomcat)
        Log.clear()
    }

    object Log {
        private val messages = mutableListOf<String>()

        fun add(message: String) {
            messages.add(message)
        }

        fun clear() {
            messages.clear()
        }

        fun messages(): List<String> = messages
    }

    class SampleServletContainerInitializer : ServletContainerInitializer {
        override fun onStartup(interestedClasses: MutableSet<Class<*>>?, servletContext: ServletContext?) {
            Log.add("ServletContainerInitializer invoked")
            interestedClasses?.forEach {
                if (AppInitializer::class.java.isAssignableFrom(it)) {
                    val initializer = it.getDeclaredConstructor()
                        .newInstance() as AppInitializer

                    if (servletContext != null) {
                        initializer.onStartup(servletContext)
                    }
                }
            }
        }
    }

    class SampleAppInitializer : AppInitializer {
        override fun onStartup(servletContext: ServletContext) {
            Log.add("AppInitializer invoked")
        }
    }

    interface AppInitializer {
        fun onStartup(servletContext: ServletContext)
    }
}
