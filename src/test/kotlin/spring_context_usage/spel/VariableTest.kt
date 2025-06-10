package spring_context_usage.spel

import org.springframework.beans.factory.config.BeanExpressionContext
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.expression.BeanFactoryResolver
import org.springframework.expression.common.TemplateParserContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import kotlin.test.Test
import kotlin.test.assertEquals

class VariableTest {
    private val parser = SpelExpressionParser()

    @Test
    fun `#root 로 root context object 에 접근할 수 있다`() {
        val context = StandardEvaluationContext(Sample())

        val hello = parse("#root.message").getValue(context)

        assertEquals("hello", hello)
    }

    @Test
    fun `#this 로 현재 context object 에 접근할 수 있다`() {
        val list = listOf(1, 2, 3, 4, 5, 6)
        val context = StandardEvaluationContext(list)

        val biggerThan3 = parse("?[#this > 3]").getValue(context)

        assertEquals(listOf(4, 5, 6), biggerThan3)
    }

    @Test
    fun `변수를 Context에 등록할 수 있다`() {
        val context = StandardEvaluationContext()
        context.setVariable("varName", "varValue")

        val varValue = parse("#varName").getValue(context)

        assertEquals("varValue", varValue)
    }

    @Test
    fun `@beanName 으로 빈을 참조할 수 있다`() {
        // Given
        val appContext = initializedApplicationContext()
        val resolver = BeanFactoryResolver(appContext)
        val evaluationContext = StandardEvaluationContext().apply { beanResolver = resolver }

        // WHen
        val sample = parse("@sample").getValue(evaluationContext) as Sample

        // Then
        assertEquals("hello", sample.message)
    }

    @Test
    fun `#{표현식} + TemplateParserContext 로 리터럴 텍스트와 혼합할 수 있다`() {
        // Given
        val evaluationContext = StandardEvaluationContext()
        val parserContext = TemplateParserContext()

        // When
        val sum = parser
            .parseExpression("1 + 2 = #{1 + 2}", parserContext)
            .getValue(evaluationContext)

        // Then
        assertEquals("1 + 2 = 3", sum)
    }

    @Test
    fun `@Value 속 #{} 표현식은 BeanExpressionResolver 에 의해 처리된다`() {
        // Given
        val appContext = initializedApplicationContext()
        val resolver = appContext.beanFactory.beanExpressionResolver
        val expressionContext = BeanExpressionContext(appContext.beanFactory, null)

        // When
        val sampleMessage = resolver?.evaluate("#{sample.message}", expressionContext)

        // Then
        assertEquals("hello", sampleMessage)
    }

    private fun initializedApplicationContext(): AnnotationConfigApplicationContext {
        return AnnotationConfigApplicationContext().apply {
            registerBeanDefinition("sample", RootBeanDefinition(Sample::class.java))
            refresh()
        }
    }

    private fun parse(expression: String) = parser.parseExpression(expression)

    // ===== 테스트용 클래스 =====

    class Sample(
        @Suppress("unused")
        val message: String = "hello"
    )
}
