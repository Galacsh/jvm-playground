package com.galacsh.spring_context_usage

import org.junit.jupiter.api.DisplayName
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.event.EventListener
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EventPublisherText {
    @Test
    @DisplayName("이벤트 퍼블리셔를 통해 이벤트를 발행할 수 있다")
    fun publish_event_with_event_publisher() {
        // Given
        val context = AnnotationConfigApplicationContext(EventHandler::class.java)
        val handler = context.getBean(EventHandler::class.java)

        // When no event is published
        // Then the handler should not have received any event
        assertFalse { handler.received }

        // When event is published
        context.publishEvent(EventPublished("hello"))

        // Then
        assertTrue { handler.received }
    }

    // ===== 테스트용 클래스 =====

    class EventPublished(val message: String)

    class EventHandler {
        var received = false
            private set

        @EventListener
        fun handle(event: EventPublished) {
            received = true
        }
    }
}
