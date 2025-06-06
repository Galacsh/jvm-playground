package spring_core_usage

import org.springframework.asm.ClassWriter
import org.springframework.asm.Opcodes
import org.springframework.asm.Type
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * spring-core는 [ASM](https://asm.ow2.io/)을 이용하여
 * 바이트코드 수준에서 클래스를 생성하고 조작할 수 있는 기능을 포함합니다.
 *
 * ASM은 Java 바이트코드를 읽고 쓰는 데 사용되는 라이브러리로,
 * 런타임에 동적으로 클래스를 생성하거나 수정할 수 있습니다.
 *
 * CGLIB, ByteBuddy 모두 내부적으로 ASM을 사용하며 더욱 편리한 API를 제공합니다.
 */
class AsmTest {
    private val className = "com.galacsh.AsmSample"
    private val methodName = "hello"
    private val methodResult = "world"
    private val methodDescriptor = "()Ljava/lang/String;"

    @Test
    fun `ASM 으로 클래스를 생성하고 메서드를 호출할 수 있음`() {
        // Given
        val asmSampleClass = writeAsmSampleClass()
        val instance = asmSampleClass.getDeclaredConstructor().newInstance()
        val method = asmSampleClass.getMethod(methodName)

        // When
        val result = method.invoke(instance) as String

        // Then
        assertEquals(methodResult, result)
    }

    private fun writeAsmSampleClass(): Class<*> {
        // 스택 프레임과 스택 맵 프레임을 자동으로 계산하도록 설정
        val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)

        startWriting(cw)
        setupConstructor(cw)
        addMethod(cw)
        endWriting(cw)

        val byteCode = cw.toByteArray()
        val classLoader = object : ClassLoader(javaClass.classLoader) {
            override fun findClass(name: String?): Class<*> {
                return if (name == className) {
                    defineClass(name, byteCode, 0, byteCode.size)
                } else {
                    super.findClass(name)
                }
            }
        }

        return classLoader.loadClass(className)
    }

    private fun addMethod(cw: ClassWriter) {
        val mv = cw.visitMethod(
            Opcodes.ACC_PUBLIC,
            methodName,
            methodDescriptor,
            null,
            null
        )
        mv.visitCode()
        // Load Constant: 피연산자 스택에 "world" 문자열을 로드
        mv.visitLdcInsn(methodResult)
        // 피연산자 스택의 최상위에 올라와 있는 객체 참조를 반환 (Return Reference - A → Address → Reference)
        mv.visitInsn(Opcodes.ARETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()
    }

    private fun endWriting(cw: ClassWriter) {
        cw.visitEnd()
    }

    private fun setupConstructor(cw: ClassWriter) {
        val constructor = cw.visitMethod(
            Opcodes.ACC_PUBLIC,
            // 바이트코드 수준에서는 생성자를 표현하기 위해 <init>이라는 특수한 이름 사용
            "<init>",
            /*
            JVM 바이트코드 수준에서는 메서드의 이름만으로는 오버로딩된 메서드들을 구분 불가
            따라서 메서드 식별을 위해 인자의 타입 순서와 반환 타입을 포함하는
            "메서드 디스크립터(Method Descriptor)" 사용.

            e.g. (IZ)Ljava/lang/String; : Integer, boolean -> String
             */
            "()V",
            null,
            null
        )
        // 메서드의 코드 부분 시작
        constructor.visitCode()
        // 인스턴스(static이 아닌) 메서드의 지역 변수 배열의 0번은 this
        // 참조를 스택에 로드(ALOAD: Load Reference - A → Address → Reference)
        constructor.visitVarInsn(Opcodes.ALOAD, 0)
        // 부모인 Object 클래스의 생성자를 호출
        constructor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            Type.getInternalName(Any::class.java),
            "<init>",
            "()V",
            false
        )
        // 메서드 종료
        constructor.visitInsn(Opcodes.RETURN)
        constructor.visitMaxs(1, 1)
        constructor.visitEnd()
    }

    private fun startWriting(cw: ClassWriter) {
        cw.visit(
            // 버전에 따라 .class 파일의 형식이 조금씩 다르므로 버전 지정이 필요합니다.
            Opcodes.V1_8,
            /*
            - ACC_PUBLIC: public 접근 제어자
            - ACC_SUPER:
                - 현대에는 interface를 제외하고 이 플래그가 거의 필수
                - 주로 생성자 호출 (<init>)이나 super 키워드를 사용한 부모 클래스 메서드 호출 시,
                  JVM이 올바른 메서드를 찾고 표준적인 동작 규칙을 따르도록 지시하는 역할
            */
            Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
            className.replace('.', '/'),
            null,
            // 부모 클래스: java.lang.Object
            Type.getInternalName(Any::class.java),
            null
        )
    }
}
