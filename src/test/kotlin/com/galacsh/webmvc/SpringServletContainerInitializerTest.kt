package com.galacsh.webmvc

import com.galacsh.support.TomcatSupport
import org.apache.catalina.startup.Tomcat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import org.springframework.web.SpringServletContainerInitializer
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.StaticWebApplicationContext
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class SpringServletContainerInitializerTest {
    private lateinit var tomcat: Tomcat

    @Test
    @DisplayName("SpringServletContainerInitializer 를 통해 초기화할 수 있다")
    fun initialize_with_spring_servlet_container_initializer() {
        // Given
        val context = TomcatSupport.addContext(tomcat, "/spring-web")
        context.addServletContainerInitializer(
            SpringServletContainerInitializer(),
            setOf(SampleWebApplicationInitializer::class.java)
        )

        // When
        tomcat.start()

        // Then
        SampleWebApplicationInitializer.also {
            assertAll(
                { assertNotNull(it.rootLevel) },
                { assertNotNull(it.servletLevel) },
            )
        }
    }

    @BeforeTest
    fun setUp() {
        tomcat = TomcatSupport.initialize(Tomcat())
    }

    @AfterTest
    fun tearDown() {
        TomcatSupport.cleanUp(tomcat)
    }

    class SampleWebApplicationInitializer : AbstractDispatcherServletInitializer() {
        companion object {
            lateinit var rootLevel: WebApplicationContext
                private set
            lateinit var servletLevel: WebApplicationContext
                private set
        }

        override fun createRootApplicationContext(): WebApplicationContext {
            rootLevel = StaticWebApplicationContext()
            return rootLevel
        }

        override fun createServletApplicationContext(): WebApplicationContext {
            servletLevel = StaticWebApplicationContext()
            return servletLevel
        }

        override fun getServletMappings(): Array<String> {
            return arrayOf("/*")
        }
    }
}
