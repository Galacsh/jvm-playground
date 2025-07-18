package com.galacsh.spring_context_usage.spel

import org.junit.jupiter.api.assertAll
import org.springframework.expression.EvaluationContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class AccessTest {
    private val parser = SpelExpressionParser()

    @Test
    fun `프로퍼티에 접근할 수 있다`() {
        // Given
        val sample = Sample("John")
        val context = StandardEvaluationContext(sample)

        // When
        val name = parse("name", context)

        // Then
        assertEquals("John", name)
    }

    @Test
    fun `메서드를 호출할 수 있다`() {
        // Given
        val sample = Sample("John")
        val context = StandardEvaluationContext(sample)

        // When
        val greetingMessage = parse("greet('!')", context)

        // Then
        assertEquals("Hello, John !", greetingMessage)
    }

    @Test
    fun `배열에 접근할 수 있다`() {
        // Given
        val array = arrayOf("apple", "banana", "cherry")
        val context = StandardEvaluationContext(array)

        // When
        val firstElement = parse("[0]", context)
        val secondElement = parse("[1]", context)

        // Then
        assertAll(
            { assertEquals("apple", firstElement) },
            { assertEquals("banana", secondElement) }
        )
    }

    @Test
    fun `리스트에 접근할 수 있다`() {
        // Given
        val list = listOf("apple", "banana", "cherry")
        val context = StandardEvaluationContext(list)

        // When
        val firstElement = parse("[0]", context)
        val secondElement = parse("get(1)", context)

        // Then
        assertAll(
            { assertEquals("apple", firstElement) },
            { assertEquals("banana", secondElement) }
        )
    }

    @Test
    fun `맵에 접근할 수 있다`() {
        // Given
        val map = mapOf("name" to "John", "age" to 123)
        val context = StandardEvaluationContext(map)

        // When
        val name = parse("['name']", context)
        val age = parse("get('age')", context)

        // Then
        assertAll(
            { assertEquals("John", name) },
            { assertEquals(123, age) }
        )
    }

    @Test
    fun `타입에 접근할 수 있다`() {
        // When
        val stringClass = parse("T(String)")
        val integerClass = parse("T(Integer)")
        val dateClass = parse("T(java.util.Date)")

        // Then
        assertEquals(String::class.java, stringClass)
        assertEquals(Integer::class.java, integerClass)
        assertEquals(Date::class.java, dateClass)
    }

    @Test
    fun `정적 메서드에 접근할 수 있다`() {
        // When
        val ten = parse("T(java.lang.Math).min(10, 20)")

        // Then
        assertEquals(10, ten)
    }

    private fun parse(expression: String) = parser.parseExpression(expression).value
    private fun parse(expression: String, context: EvaluationContext) =
        parser.parseExpression(expression).getValue(context)

    // ===== 테스트용 클래스 =====

    class Sample(val name: String) {
        @Suppress("unused")
        fun greet(suffix: String): String {
            return "Hello, $name $suffix"
        }
    }
}
