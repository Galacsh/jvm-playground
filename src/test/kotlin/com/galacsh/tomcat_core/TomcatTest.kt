package com.galacsh.tomcat_core

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("공유 Tomcat 인스턴스를 사용한 테스트들")
class TomcatTest {

    @Nested
    inner class TomcatBasicTest : AbstractTomcatBasicTest()

    @Nested
    inner class ExampleDispatcherServletTest : AbstractExampleDispatcherServletTest()

    @Nested
    inner class FilterTest : AbstractFilterTest()

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            SharedTomcat.initialize()
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            SharedTomcat.cleanUp()
        }
    }
}
