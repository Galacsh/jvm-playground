package com.galacsh.etc

import org.junit.jupiter.api.assertAll
import java.text.DecimalFormat
import kotlin.test.Test
import kotlin.test.assertEquals

class DecimalFormatTest {
    @Test
    fun `음수와 양수 각각 패턴을 지정할 수 있다`() {
        val f = DecimalFormat("[+]#;[-]#")

        assertAll(
            { assertEquals("[+]123456", f.format(123_456)) },
            { assertEquals("[-]123456", f.format(-123_456)) },
        )
    }

    @Test
    fun `Prefix, Suffix를 바꿀 수 있다`() {
        val f = DecimalFormat("[Prefix] # [Suffix]")

        assertEquals("[Prefix] 123456 [Suffix]", f.format(123_456))
    }

    @Test
    fun `항상 숫자를 표시하되 없으면 0으로 채운다`() {
        val f = DecimalFormat("0000")

        assertAll(
            { assertEquals("0000", f.format(0)) },
            { assertEquals("0001", f.format(1)) },
            { assertEquals("0012", f.format(12)) },
            { assertEquals("0123", f.format(123)) },
            { assertEquals("1234", f.format(1234)) },
        )
    }

    @Test
    fun `숫자를 표시하되 없으면 표시하지 않는다`() {
        val f = DecimalFormat("##")

        assertAll(
            { assertEquals("0", f.format(0)) },
            { assertEquals("1", f.format(1)) },
            { assertEquals("12", f.format(12)) },
            { assertEquals("123", f.format(123)) },
            { assertEquals("1234", f.format(1234)) },
        )
    }

    @Test
    fun `그룹화는 패턴의 마지막 콤마를 기준으로 정해진다`() {
        val one = DecimalFormat("#,0")
        val two = DecimalFormat("#,#0")
        val three = DecimalFormat("#,##0")
        val four = DecimalFormat("#,###0")

        assertAll(
            // #,#
            { assertEquals("1", one.format(1)) },
            { assertEquals("1,2", one.format(12)) },
            { assertEquals("1,2,3", one.format(123)) },
            // #,##
            { assertEquals("1", two.format(1)) },
            { assertEquals("12", two.format(12)) },
            { assertEquals("1,23", two.format(123)) },
            { assertEquals("12,34", two.format(1234)) },
            { assertEquals("1,23,45", two.format(12345)) },
            // #,###
            { assertEquals("1", three.format(1)) },
            { assertEquals("12", three.format(12)) },
            { assertEquals("123", three.format(123)) },
            { assertEquals("1,234", three.format(1234)) },
            { assertEquals("12,345", three.format(12345)) },
            { assertEquals("123,456", three.format(123456)) },
            { assertEquals("1,234,567", three.format(1234567)) },
            // #,####
            { assertEquals("1", four.format(1)) },
            { assertEquals("12", four.format(12)) },
            { assertEquals("123", four.format(123)) },
            { assertEquals("1234", four.format(1234)) },
            { assertEquals("1,2345", four.format(12345)) },
            { assertEquals("12,3456", four.format(123456)) },
            { assertEquals("123,4567", four.format(1234567)) },
            { assertEquals("1234,5678", four.format(12345678)) },
            { assertEquals("1,2345,6789", four.format(123456789)) },
        )
    }

    @Test
    fun `소수부 패턴도 지정할 수 있다`() {
        val f = DecimalFormat("0.##")

        assertAll(
            { assertEquals("0", f.format(0)) },
            { assertEquals("0.1", f.format(0.1)) },
            { assertEquals("0.12", f.format(0.12)) },
        )
    }

    @Test
    fun `소수부도 0으로 채울 수 있다`() {
        val f = DecimalFormat("0.000")

        assertAll(
            { assertEquals("0.000", f.format(0)) },
            { assertEquals("0.100", f.format(0.1)) },
            { assertEquals("0.120", f.format(0.12)) },
            { assertEquals("0.123", f.format(0.123)) },
        )
    }

    @Test
    fun `소수부의 경우 자리수를 넘어가면 반올림이 발생한다`() {
        val f = DecimalFormat("0.##")

        assertAll(
            { assertEquals("0", f.format(0)) },
            { assertEquals("0.1", f.format(0.1)) },
            { assertEquals("0.12", f.format(0.12)) },
            { assertEquals("0.12", f.format(0.125)) },
            { assertEquals("0.13", f.format(0.1251)) },
            { assertEquals("0.13", f.format(0.126)) },
        )
    }

    @Test
    fun `지수 표기법을 사용할 수 있다`() {
        val f = DecimalFormat("0.##E0")

        assertAll(
            { assertEquals("1E0", f.format(1)) },
            { assertEquals("1.2E1", f.format(12)) },
            { assertEquals("1.23E2", f.format(123)) },
        )
    }

    @Test
    fun `지수 표기법 사용 시에도 반올림이 발생한다`() {
        val f = DecimalFormat("0.##E0")

        assertAll(
            { assertEquals("1E0", f.format(1)) },
            { assertEquals("1.2E1", f.format(12)) },
            { assertEquals("1.23E2", f.format(123)) },
            { assertEquals("1.23E3", f.format(1234)) },
            { assertEquals("1.24E3", f.format(1236)) },
        )
    }
}
