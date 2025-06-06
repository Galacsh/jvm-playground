package spring_core_usage

import org.springframework.objenesis.ObjenesisStd
import org.springframework.objenesis.strategy.StdInstantiatorStrategy
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * spring-core는 [Objenesis](https://objenesis.org/details.html)를 이용하여
 * 객체 생성 시 생성자를 우회할 수 있는 기능을 포함합니다.
 * 이 기능은 주로 테스트나 프록시 생성에 사용됩니다.
 *
 * 객체가 생성되는 방식은 StdInstantiatorStrategy에 의해 결정됩니다.
 * 전략은 다음과 같습니다:
 *
 * - sun.reflect.ReflectionFactory 기반 전략
 *     - Java 객체를 직렬화 → 역직렬화 메커니즘 이용
 *     - 생성자를 호출하지 않고 단순히 메모리를 할당하고 필드 값을 복원
 * - sun.misc.Unsafe 기반 전략
 *     - Unsafe API: JVM이 내부적으로 사용하는 매우 강력하고 낮은 수준의 API 집합
 *     - 이 중 allocateInstance 메서드를 사용하여 객체를 생성
 * - 여러 JVM 별 전략 (안드로이드, GNU Classpath 기반 JVM 등)
 *
 * @see StdInstantiatorStrategy
 */
class ObjenesisTest {
    @Test
    fun `objenesis can instantiate uninstantiable classes`() {
        // private 생성자에다 init 블록에서 예외를 던지는 클래스도 생성 가능
        class Sample private constructor(val name: String) {
            init {
                throw RuntimeException("a")
            }
        }

        val objenesis = ObjenesisStd()
        val instantiator = objenesis.getInstantiatorOf(Sample::class.java)
        val instance: Sample = instantiator.newInstance()

        assertNotNull(instance)
        assertNull(instance.name)
    }
}
