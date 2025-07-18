package com.galacsh.spring_core_usage.core

import org.junit.jupiter.api.Disabled
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.SimpleAsyncTaskExecutor
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * TaskExecutor 를 이용하여 비동기 작업을 실행할 수 있습니다.
 * 얼핏보면 ExecutorService와 비슷해 보이기 때문에 왜 따로 구현했는지 의문이 들 수 있습니다.
 *
 * 따로 구현된 이유는 다음과 같다고 볼 수 있습니다.
 *
 * - Executor 및 ExecutorService와 같은 강력한 동시성 유틸리티가 도입된 것은 Java 5부터
 * - 스프링 프레임워크는 Java 5 이전부터 존재했으므로 표준화된 방법이 필요했음
 * - Java 5 이후에는 내부적으로 ExecutorService를 사용하여 구현됨
 *
 * 이로 인해 자체적으로 정의된 TaskExecutor 인터페이스는 다음과 같은 장점을 가집니다.
 *
 * - 하위 버전 호환
 * - 다양한 환경에서 일관된 방식으로 비동기 작업 처리 가능
 * - TaskExecutor 구현체를 빈으로 관리하여 손쉽게 교체할 수 있음
 * - 스프링과 긴밀하게 통합 → @Async / 스프링 컨테이너 종료 시 라이프사이클 관리 등
 */
@Disabled // 1초 소요되므로 테스트 비활성화
class TaskTest {
    @Test
    fun `TaskExecutor 를 사용하여 비동기 작업을 실행할 수 있다`() {
        // Given
        val iterate = 5
        val taskDuration = 1000L
        val latch = CountDownLatch(iterate)
        val executor = SimpleAsyncTaskExecutor()

        // When
        val elapsed = measureTimeMillis {
            for (i in 1..iterate) {
                executeWith(executor) {
                    Thread.sleep(taskDuration)
                    latch.countDown()
                }
            }
            latch.await()
        }

        // Then
        assertTrue { elapsed < taskDuration * iterate }
    }

    private fun <T> executeWith(executor: AsyncTaskExecutor, task: Callable<T>): CompletableFuture<T> {
        return executor.submitCompletable(task)
    }
}
