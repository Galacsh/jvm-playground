package com.galacsh.spring_core

import org.junit.jupiter.api.DisplayName
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * AoT(Ahead-of-Time) 컴파일은 미리 코드를 분석하고 최적화하여 컴파일하는 것을 말합니다.
 * 이때, AoT 컴파일은 JVM 방식과 Native Image(실행 파일)로 생성하는 방식이 있습니다.
 *
 * - Native Image(실행 파일) 생성 방식
 *     - 자바 컴파일러를 통해 바이트코드 생성
 *     - 모든 클래스와 의존성 파악 후 정적 분석을 통해 실행 중 도달 가능한 클래스와 메서드만 파악
 *     - 정적 분석만으로는 리플렉션, JNI, 프록시, 리소스 로딩 등 런타임에 필요한 정보가 부족한 경우
 *         - native-image-agent(Java Agent)를 사용해 실행하여 실행 시점에 필요한 힌트들을 수집
 *         - 또는 코드 상에서 `RuntimeHintsRegistrar`를 사용해 포함되어야 힌트들을 명시적으로 지정
 *         - 수집된 힌트들을 파일로 저장 (예: `reflect-config.json`, `resource-config.json`)
 *     - 정적 분석 정보와 힌트들을 이용해 특정 OS와 아키텍처에 최적화된 실행 파일 생성
 *     - (참고) 실행 파일도 내부적으로 훨씬 가벼운 SubstrateVM 을 이용 → 스레드, 메모리 관리 등
 * - JVM 방식
 *     - AoT 처리 시 생성되는 파일들을 함께 빌드하여 JVM 에서 실행
 *         - Java Source Code: e.g. Bean 정의하는 코드 (`@Configuration + @Bean`)
 *             - 원래라면, 프로그램 시작 시 리플렉션으로 파싱하고 Bean 정의를 생성
 *             - 이를 미리 처리하여 "Bean 정의" 클래스를 생성해두는 것
 *         - Bytecode: e.g. 런타임에 생성될 프록시
 *     - 컴파일 시점에 최적화된 바이트코드 생성
 *     - 최종 결과물은 JVM 에서 실행 가능한 `.class`/`.jar` 파일
 *     - JVM을 통해 실행되므로 빠른 시작 시간을 확보하면서 JVM의 장점 유지
 *
 * 다만 위 방식 모두 제한사항이 있습니다.
 *
 * - 정의된 빈은 런타임에 변경될 수 없음
 *     - AoT 처리 시점에 Bean 정의가 고정됨
 *     - 즉, @Profile, @Conditional 등이 컴파일 시점에 결정됨
 */
class AotTest {
    @Test
    @DisplayName("AoT 힌트")
    fun aot_hints() {
        // Given
        val hints = RuntimeHints()
        val registrar = RuntimeHintsRegistrar { runtimeHints, _ ->
            runtimeHints
                .resources()
                .registerPattern("spring_core/*.json")
        }

        // When
        registrar.registerHints(hints, javaClass.classLoader)

        // Then
        assertTrue {
            RuntimeHintsPredicates.resource()
                .forResource("spring_core/variables.json")
                .test(hints)
        }
    }
}
