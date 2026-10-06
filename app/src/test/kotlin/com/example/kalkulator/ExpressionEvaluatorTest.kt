package com.example.kalkulator

import com.example.kalkulator.domain.Calculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ExpressionEvaluatorTest {

    @Test
    fun testSimpleAddition() {
        val result = Calculator.evaluate("2 + 3")
        assertEquals(5.0, result, 0.00001)
    }

    @Test
    fun testSimpleSubtraction() {
        val result = Calculator.evaluate("10 - 4")
        assertEquals(6.0, result, 0.00001)
    }

    @Test
    fun testSimpleMultiplication() {
        val result = Calculator.evaluate("3 * 7")
        assertEquals(21.0, result, 0.00001)
    }

    @Test
    fun testSimpleDivision() {
        val result = Calculator.evaluate("8 / 2")
        assertEquals(4.0, result, 0.00001)
    }

    @Test
    fun testSimpleModulo() {
        val result = Calculator.evaluate("10 % 3")
        assertEquals(1.0, result, 0.00001)
    }

    @Test
    fun testLeftAssociativityForSubtraction() {
        val result = Calculator.evaluate("10 - 4 - 2")
        assertEquals(4.0, result, 0.00001)
    }

    @Test
    fun testLeftAssociativityForDivision() {
        val result = Calculator.evaluate("24 / 4 / 2")
        assertEquals(3.0, result, 0.00001)
    }

    @Test
    fun testOperatorPrecedenceMultiplicationOverAddition() {
        val result = Calculator.evaluate("2 + 3 * 4")
        assertEquals(14.0, result, 0.00001)
    }

    @Test
    fun testOperatorPrecedenceDivisionOverSubtraction() {
        val result = Calculator.evaluate("20 - 4 / 2")
        assertEquals(18.0, result, 0.00001)
    }

    @Test
    fun testOperatorPrecedenceComplexChain() {
        val result = Calculator.evaluate("100 - 20 * 3 + 10 / 2")
        assertEquals(45.0, result, 0.00001)
    }

    @Test
    fun testParenthesesOverridePrecedence() {
        val result = Calculator.evaluate("(2 + 3) * 4")
        assertEquals(20.0, result, 0.00001)
    }

    @Test
    fun testNestedParentheses() {
        val result = Calculator.evaluate("2 * (3 + 4 * (2 + 1))")
        assertEquals(30.0, result, 0.00001)
    }

    @Test
    fun testUnaryMinusAtBeginning() {
        val result = Calculator.evaluate("-5 + 8")
        assertEquals(3.0, result, 0.00001)
    }

    @Test
    fun testUnaryMinusAfterOperator() {
        val result = Calculator.evaluate("2 * -3")
        assertEquals(-6.0, result, 0.00001)
    }

    @Test
    fun testUnaryMinusWithParentheses() {
        val result = Calculator.evaluate("-(2 + 3) * 4")
        assertEquals(-20.0, result, 0.00001)
    }

    @Test
    fun testDecimalNumbers() {
        val result = Calculator.evaluate("2.5 * 4 + 1.25")
        assertEquals(11.25, result, 0.00001)
    }

    @Test
    fun testExpressionWithoutSpaces() {
        val result = Calculator.evaluate("2+3*4-(5/2)")
        assertEquals(11.5, result, 0.00001)
    }

    @Test
    fun testDivisionByZeroThrowsArithmeticException() {
        val exception = assertThrows(ArithmeticException::class.java) {
            Calculator.evaluate("10 / 0")
        }
        assertEquals("division_by_zero", exception.message)
    }

    @Test
    fun testModuloByZeroThrowsArithmeticException() {
        val exception = assertThrows(ArithmeticException::class.java) {
            Calculator.evaluate("10 % 0")
        }
        assertEquals("modulo_by_zero", exception.message)
    }

    @Test
    fun testUnmatchedParenthesisThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException::class.java) {
            Calculator.evaluate("(2 + 3")
        }
    }

    @Test
    fun testMissingClosingParenthesisThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException::class.java) {
            Calculator.evaluate("2 + 3)")
        }
    }

    @Test
    fun testInvalidCharacterThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException::class.java) {
            Calculator.evaluate("2 & 3")
        }
    }

    @Test
    fun testBlankExpressionThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException::class.java) {
            Calculator.evaluate("   ")
        }
    }
}
