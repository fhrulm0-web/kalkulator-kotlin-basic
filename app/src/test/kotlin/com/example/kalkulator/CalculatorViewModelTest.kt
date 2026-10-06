package com.example.kalkulator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CalculatorViewModelTest {

    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setUp() {
        viewModel = CalculatorViewModel()
    }

    @Test
    fun testInitialState() {
        val state = viewModel.uiState.value
        assertEquals("", state.expression)
        assertNull(state.result)
        assertNull(state.errorResId)
    }

    @Test
    fun testOnAppendBuildsExpression() {
        viewModel.onAppend("2")
        viewModel.onAppend("+")
        viewModel.onAppend("3")
        viewModel.onAppend("*")
        viewModel.onAppend("4")

        assertEquals("2+3*4", viewModel.uiState.value.expression)
    }

    @Test
    fun testOnBackspaceRemovesLastCharacter() {
        viewModel.onAppend("1")
        viewModel.onAppend("2")
        viewModel.onAppend("+")
        viewModel.onBackspace()

        assertEquals("12", viewModel.uiState.value.expression)
    }

    @Test
    fun testOnClearResetsAll() {
        viewModel.onAppend("5")
        viewModel.onAppend("*")
        viewModel.onAppend("6")
        viewModel.onCalculate()
        viewModel.onClear()

        val state = viewModel.uiState.value
        assertEquals("", state.expression)
        assertNull(state.result)
        assertNull(state.errorResId)
    }

    @Test
    fun testCalculateValidExpression() {
        viewModel.onAppend("2")
        viewModel.onAppend("+")
        viewModel.onAppend("3")
        viewModel.onAppend("*")
        viewModel.onAppend("4")
        viewModel.onCalculate()

        val state = viewModel.uiState.value
        assertEquals("14", state.result)
        assertNull(state.errorResId)
    }

    @Test
    fun testCalculateWithParentheses() {
        viewModel.onAppend("(")
        viewModel.onAppend("2")
        viewModel.onAppend("+")
        viewModel.onAppend("3")
        viewModel.onAppend(")")
        viewModel.onAppend("*")
        viewModel.onAppend("4")
        viewModel.onCalculate()

        val state = viewModel.uiState.value
        assertEquals("20", state.result)
        assertNull(state.errorResId)
    }

    @Test
    fun testCalculateDivisionByZeroSetsError() {
        viewModel.onAppend("1")
        viewModel.onAppend("0")
        viewModel.onAppend("/")
        viewModel.onAppend("0")
        viewModel.onCalculate()

        val state = viewModel.uiState.value
        assertNull(state.result)
        assertEquals(R.string.error_division_by_zero, state.errorResId)
    }

    @Test
    fun testCalculateModuloByZeroSetsError() {
        viewModel.onAppend("1")
        viewModel.onAppend("0")
        viewModel.onAppend("%")
        viewModel.onAppend("0")
        viewModel.onCalculate()

        val state = viewModel.uiState.value
        assertNull(state.result)
        assertEquals(R.string.error_modulo_by_zero, state.errorResId)
    }

    @Test
    fun testCalculateInvalidExpressionSetsError() {
        viewModel.onAppend("2")
        viewModel.onAppend("+")
        viewModel.onAppend("+")
        viewModel.onCalculate()

        val state = viewModel.uiState.value
        assertNull(state.result)
        assertEquals(R.string.error_invalid_expression, state.errorResId)
    }

    @Test
    fun testAppendOperatorAfterCalculationChainsResult() {
        viewModel.onAppend("4")
        viewModel.onAppend("*")
        viewModel.onAppend("5")
        viewModel.onCalculate()

        assertEquals("20", viewModel.uiState.value.result)

        // User taps "+" to continue calculation
        viewModel.onAppend("+")
        viewModel.onAppend("3")
        viewModel.onCalculate()

        assertEquals("23", viewModel.uiState.value.result)
    }
}
