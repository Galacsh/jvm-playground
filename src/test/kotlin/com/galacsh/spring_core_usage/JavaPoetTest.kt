package com.galacsh.spring_core_usage

import org.junit.jupiter.api.DisplayName
import org.springframework.javapoet.JavaFile
import org.springframework.javapoet.MethodSpec
import org.springframework.javapoet.TypeSpec
import javax.lang.model.element.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * spring-core는 JavaPoet를 이용하여
 * Java 소스 파일을 생성할 수 있는 기능을 포함합니다.
 *
 * JavaPoet는 Java 소스 코드를 생성하기 위한 라이브러리로,
 * Java 클래스를 프로그래밍적으로 생성하고 조작할 수 있는 API를 제공합니다.
 */
class JavaPoetTest {
    private val packageName = "com.galacsh"
    private val className = "PoetSample"

    @Test
    @DisplayName("JavaPoet을 통해 java 소스 파일을 생성할 수 있음")
    fun generate_java_source_with_javapoet() {
        // public static void main(String[] args)
        val mainMethod = MethodSpec.methodBuilder("main")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(Void.TYPE)
            .addParameter(Array<String>::class.java, "args")
            .addStatement("\$T.out.println(\$S)", System::class.java, "Hello, JavaPoet!")
            .build()

        val clazz = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC)
            .addMethod(mainMethod)
            .build()

        val javaFile = JavaFile.builder(packageName, clazz).build()

        assertEquals(
            """
                package com.galacsh;

                import java.lang.String;
                import java.lang.System;

                public class PoetSample {
                  public static void main(String[] args) {
                    System.out.println("Hello, JavaPoet!");
                  }
                }
            """.trimIndent(),
            javaFile.toString().trimIndent()
        )
    }
}
