package com.galacsh.spring_core_usage.core

import org.junit.jupiter.api.assertAll
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.core.annotation.MergedAnnotations
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.test.*

/**
 * spring-core는 어노테이션을 처리하는 유틸리티를 제공합니다.
 */
class AnnotationUtilsTest {
    @Test
    fun `클래스 어노테이션을 찾아서 값을 확인할 수 있다`() {
        val classSimpleAnnotation = AnnotationUtils.findAnnotation(
            AnnotatedBaseClass::class.java,
            SimpleAnnotation::class.java
        )
        assertNotNull(classSimpleAnnotation)
        assertAll(
            { assertEquals("class_level_annotation", classSimpleAnnotation.value) },
            { assertEquals(1, classSimpleAnnotation.count) },
        )
    }

    @Test
    fun `메서드 어노테이션을 찾아서 값을 확인할 수 있다`() {
        val methodSimpleAnnotation = AnnotationUtils.findAnnotation(
            AnnotatedBaseClass::class.java.getMethod("annotatedMethod"),
            SimpleAnnotation::class.java
        )
        assertNotNull(methodSimpleAnnotation)
        assertAll(
            { assertEquals("method_level_annotation", methodSimpleAnnotation.value) },
            { assertEquals(2, methodSimpleAnnotation.count) },
        )
    }

    @Test
    fun `필드 어노테이션을 찾아서 값을 확인할 수 있다`() {
        val fieldSimpleAnnotation = AnnotationUtils.findAnnotation(
            AnnotatedBaseClass::class.java.getField("annotatedField"),
            SimpleAnnotation::class.java
        )
        assertNotNull(fieldSimpleAnnotation)
        assertAll(
            { assertEquals("field_level_annotation", fieldSimpleAnnotation.value) },
            { assertEquals(3, fieldSimpleAnnotation.count) },
        )
    }

    @Test
    fun `직접 정의된 어노테이션임을 알 수 있다`() {
        val declaredLocally = AnnotationUtils.isAnnotationDeclaredLocally(
            SimpleAnnotation::class.java,
            AnnotatedBaseClass::class.java,
        )
        val notDeclaredLocally = AnnotationUtils.isAnnotationDeclaredLocally(
            SimpleAnnotation::class.java,
            AnnotatedSubClass::class.java
        )

        assertTrue { declaredLocally }
        assertFalse { notDeclaredLocally }
    }

    @Test
    fun `FQCN 으로 어노테이션 여부를 알 수 있다`() {
        val hasSimpleAnnotation = AnnotatedElementUtils.isAnnotated(
            AnnotatedBaseClass::class.java,
            "com.galacsh.spring_core_usage.core.AnnotationUtilsTest\$ComposedAnnotation"
        )

        assertTrue { hasSimpleAnnotation }
    }

    @Test
    fun `Composed 속 Marker 어노테이션을 찾을 수 있다`() {
        val markerAnnotation = AnnotatedElementUtils.findMergedAnnotation(
            AnnotatedBaseClass::class.java,
            MarkerAnnotation::class.java
        )

        assertNotNull(markerAnnotation)
        assertEquals("composed", markerAnnotation.name)
    }

    @Test
    fun `상속된 클래스에서도 Composed 속 Marker 어노테이션을 찾을 수 있다`() {
        val markerAnnotation = AnnotatedElementUtils.findMergedAnnotation(
            AnnotatedSubClass::class.java,
            MarkerAnnotation::class.java
        )

        assertNotNull(markerAnnotation)
        assertEquals("composed", markerAnnotation.name)
    }

    @Test
    fun `한 번에 묶어서 원하는 어노테이션의 값을 찾을 수 있다`() {
        val merged = MergedAnnotations.from(AnnotatedBaseClass::class.java)

        val markerName = merged.get(MarkerAnnotation::class.java)
            .getString("name")

        assertEquals("composed", markerName)
    }

    // ===== 테스트용 어노테이션 =====

    @Retention(RUNTIME)
    @Target(CLASS, FUNCTION, FIELD)
    annotation class SimpleAnnotation(
        val value: String = "default",
        val count: Int = 0
    )

    /**
     * meta 어노테이션 - 다른 어노테이션을 어노테이션하는 어노테이션
     */
    @Retention(RUNTIME)
    @Target(CLASS)
    annotation class MarkerAnnotation(
        val name: String = "marker"
    )

    /**
     * meta 어노테이션을 사용하는 어노테이션.
     * `AnnotatedElementUtils`와 `MergedAnnotations`를 통해서
     * meta 어노테이션 계층 구조를 탐색하는 것을 보여주는데 사용됩니다.
     */
    @Retention(RUNTIME)
    @Target(CLASS)
    @MarkerAnnotation(name = "composed")
    annotation class ComposedAnnotation(val description: String = "composed description")

    /**
     * 특정 클래스에 이 어노테이션이 적용된 경우, subclass 에서도
     * 이 어노테이션이 자동적으로 적용됩니다. (@Inherited 어노테이션을 사용하였기 때문)
     */
    @Retention(RUNTIME)
    @Target(CLASS)
    @Inherited
    annotation class HierarchyAnnotation(val inheritedValue: String = "inherited_default")

    // ===== 테스트용 클래스 =====

    @SimpleAnnotation(value = "class_level_annotation", count = 1)
    @ComposedAnnotation(description = "class_composed_description")
    @HierarchyAnnotation(inheritedValue = "class_inherited_value")
    open class AnnotatedBaseClass {
        @SimpleAnnotation(value = "method_level_annotation", count = 2)
        fun annotatedMethod() {
            // Method body
        }

        @SimpleAnnotation(value = "field_level_annotation", count = 3)
        @JvmField
        var annotatedField: String = "sample data"
    }

    class AnnotatedSubClass : AnnotatedBaseClass()
}
