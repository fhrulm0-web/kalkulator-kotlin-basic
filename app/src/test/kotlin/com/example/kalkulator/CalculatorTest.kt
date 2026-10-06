package com.example.kalkulator

import com.example.kalkulator.domain.Calculator
import com.example.kalkulator.domain.Operation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class CalculatorTest {

    @Test
    fun testAddition() {
        val result = Calculator.calculate(15.0, 27.0, Operation.ADD)
        assertEquals(42.0, result, 0.00001)
    }

    @Test
    fun testSubtraction() {
        val result = Calculator.calculate(50.0, 18.0, Operation.SUBTRACT)
        assertEquals(32.0, result, 0.00001)
    }

    @Test
    fun testMultiplication() {
        val result = Calculator.calculate(12.5, 4.0, Operation.MULTIPLY)
        assertEquals(50.0, result, 0.00001)
    }

    @Test
    fun testDivision() {
        val result = Calculator.calculate(10.0, 4.0, Operation.DIVIDE)
        assertEquals(2.5, result, 0.00001)
    }

    @Test
    fun testModulo() {
        val result = Calculator.calculate(10.0, 3.0, Operation.MODULO)
        assertEquals(1.0, result, 0.00001)
    }

    @Test
    fun testDivisionByZeroThrowsException() {
        assertThrows(ArithmeticException::class.java) {
            Calculator.calculate(10.0, 0.0, Operation.DIVIDE)
        }
    }

    @Test
    fun testModuloByZeroThrowsException() {
        assertThrows(ArithmeticException::class.java) {
            Calculator.calculate(10.0, 0.0, Operation.MODULO)
        }
    }

    @Test
    fun testParseOperation() {
        assertEquals(Operation.ADD, Calculator.parseOperation("+"))
        assertEquals(Operation.SUBTRACT, Calculator.parseOperation("-"))
        assertEquals(Operation.MULTIPLY, Calculator.parseOperation("*"))
        assertEquals(Operation.DIVIDE, Calculator.parseOperation("/"))
        assertEquals(Operation.MODULO, Calculator.parseOperation("%"))
        assertNull(Calculator.parseOperation("^"))
    }

    // --- Tests for formatNumber ---

    @Test
    fun testFormatNumberInteger() {
        assertEquals("42", Calculator.formatNumber(42.0))
    }

    @Test
    fun testFormatNumberDecimal() {
        assertEquals("2.5", Calculator.formatNumber(2.5))
    }

    @Test
    fun testFormatNumberTenBillion() {
        assertEquals("10000000000", Calculator.formatNumber(1e10))
    }

    @Test
    fun testFormatNumberSmallDecimal() {
        assertEquals("0.0000001", Calculator.formatNumber(0.0000001))
    }

    @Test
    fun testFormatNumberLargeInteger() {
        assertEquals("100000000000000000000", Calculator.formatNumber(1e20))
    }

    @Test
    fun testFormatNumberNegativeZero() {
        assertEquals("0", Calculator.formatNumber(-0.0))
    }

    @Test
    fun testFormatNumberVeryLargeNoE() {
        val formatted = Calculator.formatNumber(1e20)
        assertFalse("Output tidak boleh mengandung notasi E", formatted.contains('E', ignoreCase = true))
    }
}
