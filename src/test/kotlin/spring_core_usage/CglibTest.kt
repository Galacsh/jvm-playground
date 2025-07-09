package spring_core_usage

import org.junit.jupiter.api.Disabled
import org.springframework.cglib.proxy.Enhancer
import org.springframework.cglib.proxy.MethodInterceptor
import org.springframework.cglib.proxy.MethodProxy
import java.lang.reflect.Method
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * spring-core는 cglib를 이용하여
 * 런타임에 클래스의 프록시를 생성할 수 있는 기능을 포함합니다.
 * 바이트코드를 조작하여 객체/클래스를 동적으로 생성하거나 프록시 객체를 생성할 수 있습니다.
 *
 * 참고로, Java 9 이상에서는 cglib를 사용하기 위해 다음 옵션을 추가해야 합니다.
 *
 * `--add-opens=<모듈명>/<패키지명>=<대상_모듈 또는 ALL-UNNAMED>`
 *
 * 이 옵션을 추가하는 이유는 Java 9부터 모듈 시스템(Java Platform Module System, JPMS)이
 * 추가되면서 JDK의 내부 API에 대한 강력한 캡슐화를 적용되었기 때문이고,
 * cglib이 `defineClass`와 같은 JDK 내부 API를 사용하기 때문에
 * `--add-opens` 옵션을 통해 해당하는 내부 API에 대한 접근 권한을 열어주어야 합니다.
 *
 * 다만, Spring Boot 의 경우 Gradle 플러그인을 통해 빌드 시
 * Manifest 파일에 자동으로 `--add-opens` 옵션을 추가해주기 때문에
 * 별도로 설정하지 않아도 됩니다.
 */
@Disabled(
    """
    테스트를 위해서는 @Disabled 어노테이션을 제거하고,
    다음 JVM 옵션과 함께 실행해야 합니다:
        --add-opens=java.base/java.lang=ALL-UNNAMED
"""
)
class CglibTest {
    @Test
    fun `cglib 이용한 클래스 생성`() {
        // Given
        val enhancer = Enhancer()
        enhancer.setInterfaces(arrayOf(CglibSample::class.java))
        enhancer.setCallback(worldOnHello())

        // When
        val instance = enhancer.create() as CglibSample

        // Then
        assertEquals("world", instance.hello())
    }

    @Test
    fun `cglib 이용한 클래스 프록시 생성`() {
        // Given
        val enhancer = Enhancer()
        enhancer.setSuperclass(ProxyTarget::class.java)
        enhancer.setCallback(worldOnHello())

        // When
        val proxyInstance = enhancer.create() as ProxyTarget

        // Then
        assertEquals("world", proxyInstance.hello())
    }

    interface CglibSample {
        fun hello(): String
    }

    open class ProxyTarget {
        open fun hello(): String {
            return "This should not be returned"
        }
    }

    private fun worldOnHello() =
        MethodInterceptor { _: Any?, method: Method?, _: Array<out Any>?, _: MethodProxy? ->
            when (method?.name) {
                "hello" -> "world"
                else -> throw UnsupportedOperationException("Method not supported: ${method?.name}")
            }
        }
}
