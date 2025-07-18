package com.galacsh.spring_context

import org.junit.jupiter.api.DisplayName
import org.springframework.context.annotation.*
import org.springframework.core.type.AnnotationMetadata
import kotlin.test.Test
import kotlin.test.assertEquals

class EnableConfigurationTest {
    @Test
    @DisplayName("특정 빈 설정을 로드하는 어노테이션을 만들 수 있다")
    fun load_bean_config_with_custom_annotation() {
        val ac = AnnotationConfigApplicationContext(SimpleConfig::class.java)
        val hello = ac.getBean(Greet::class.java)
        assertEquals("Hello, World!", hello.greet())
    }

    @Test
    @DisplayName("ImportSelector 통해 빈 설정을 선택할 수 있다")
    fun select_bean_config_with_import_selector() {
        val ac = AnnotationConfigApplicationContext(UsingSelectorConfig::class.java)
        val hello = ac.getBean(Greet::class.java)
        assertEquals("Hello, John!", hello.greet())
    }

    @EnableGreetWorld
    @Configuration
    open class SimpleConfig

    @EnableGreetWithSelector(value = "John")
    @Configuration
    open class UsingSelectorConfig

    @Import(GreetWorldConfig::class)
    annotation class EnableGreetWorld

    @Import(GreetSelector::class)
    annotation class EnableGreetWithSelector(val value: String)

    class GreetSelector : ImportSelector {
        override fun selectImports(metadata: AnnotationMetadata): Array<String> {
            val attrs = metadata.getAnnotationAttributes(EnableGreetWithSelector::class.java.name)
            val value = attrs?.get("value") as String
            return when (value) {
                "World" -> arrayOf(GreetWorldConfig::class.java.name)
                "John" -> arrayOf(GreetJohnConfig::class.java.name)
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }

    @Configuration
    open class GreetWorldConfig {
        @Bean
        open fun greet() = Greet(User("World"))
    }

    @Configuration
    open class GreetJohnConfig {
        @Bean
        open fun greet() = Greet(User("John"))
    }

    class Greet(val user: User) {
        fun greet() = "Hello, ${user.name}!"
    }

    class User(var name: String)
}
