package com.galacsh.spring_context_usage.spel

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import org.springframework.expression.EvaluationContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OperationsTest {
    private val parser = SpelExpressionParser()

    @Test
    @DisplayName("리터럴 표현식")
    fun literal_expression() {
        assertAll(
            { assertEquals("Hello World", parse("'Hello World'")) },
            { assertEquals(123, parse("123")) },
            { assertEquals(123.45, parse("123.45")) },
            { assertEquals(true, parse("true")) },
            { assertEquals(null, parse("null")) },
        )
    }

    @Test
    @DisplayName("논리 연산자를 사용할 수 있다")
    fun logical_operators() {
        // Given
        val sample = mapOf("count" to 10)
        val context = StandardEvaluationContext(sample)

        // When
        val isBetween5and15 = parse(
            "5 <= ['count'] and ['count'] <= 15",
            context
        ) as Boolean

        // Then
        assertTrue { isBetween5and15 }
    }

    @Test
    @DisplayName("수학 연산자를 사용할 수 있다")
    fun math_operators() {
        // Given
        val sample = mapOf("x" to 10, "y" to 5)
        val context = StandardEvaluationContext(sample)

        // When
        val sum = parse("['x'] + ['y']", context)
        val product = parse("['x'] * ['y']", context)

        // Then
        assertAll(
            { assertEquals(15, sum) },
            { assertEquals(50, product) }
        )
    }

    @Test
    @DisplayName("삼항 연산자를 사용할 수 있다")
    fun ternary_operator() {
        // Given
        val sample = mapOf("isActive" to true)
        val context = StandardEvaluationContext(sample)

        // When
        val message = parse(
            "['isActive'] ? 'active' : 'inactive'",
            context
        )

        // Then
        assertEquals("active", message)
    }

    @Test
    @DisplayName("Elvis 연산자를 사용할 수 있다")
    fun elvis_operator() {
        // Given
        val sample = mapOf("name" to "John")
        val context = StandardEvaluationContext(sample)

        // When
        val age = parse("['age'] ?: 0", context)

        // Then
        assertEquals(0, age)
    }

    @Test
    @DisplayName("안전한 탐색을 할 수 있다")
    fun safe_navigation() {
        // Given
        val sample = mapOf("name" to "john")
        val context = StandardEvaluationContext(sample)

        // When
        val familyName = parse("get('familyName')?.uppercase()", context)

        // Then
        assertEquals(null, familyName)
    }

    private fun parse(expression: String) = parser.parseExpression(expression).value
    private fun parse(expression: String, context: EvaluationContext) =
        parser.parseExpression(expression).getValue(context)
}
