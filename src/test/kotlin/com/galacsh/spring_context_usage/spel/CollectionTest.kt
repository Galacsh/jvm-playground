package com.galacsh.spring_context_usage.spel

import org.junit.jupiter.api.assertAll
import org.springframework.expression.EvaluationContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import kotlin.test.Test
import kotlin.test.assertEquals

class CollectionTest {
    private val parser = SpelExpressionParser()

    @Test
    fun `컬렉션에서 특정 필드를 뽑아낼 수 있다 - Projection`() {
        // Given
        val users = listOf(
            Sample("Alice", 10),
            Sample("Bob", 20),
            Sample("Charlie", 30),
        )
        val context = StandardEvaluationContext(users)

        // When
        val names = parse("![name]", context)
        val ages = parse("![age]", context)

        // Then
        assertAll(
            { assertEquals(listOf("Alice", "Bob", "Charlie"), names) },
            { assertEquals(listOf(10, 20, 30), ages) }
        )
    }

    @Test
    fun `컬렉션에서 조건에 맞는 요소를 필터링할 수 있다 - Selection`() {
        // Given
        val users = listOf(
            Sample("Alice", 10),
            Sample("Bob", 20),
            Sample("Charlie", 30),
        )
        val context = StandardEvaluationContext(users)

        // When
        val filteredUsers = parse("?[age > 15]", context) as List<*>

        // Then
        assertAll(
            { assertEquals(2, filteredUsers.size) },
            { assertEquals("Bob", (filteredUsers[0] as Sample).name) },
            { assertEquals("Charlie", (filteredUsers[1] as Sample).name) }
        )
    }


    private fun parse(expression: String, context: EvaluationContext) =
        parser.parseExpression(expression).getValue(context)

    // ===== 테스트용 클래스 =====

    class Sample(val name: String, val age: Int = 0)
}
