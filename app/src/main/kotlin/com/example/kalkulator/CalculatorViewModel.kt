package com.example.kalkulator

import androidx.lifecycle.ViewModel
import com.example.kalkulator.domain.Calculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CalculatorUiState(
    val expression: String = "",
    val result: String? = null,
    val previewResult: String? = null,
    val errorResId: Int? = null,
    val isEvaluated: Boolean = false
)

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val operators = setOf("+", "-", "*", "/", "%", "×", "÷")

    fun onAppend(value: String) {
        _uiState.update { current ->
            val newExpression = if (current.isEvaluated) {
                if (operators.contains(value) && current.result != null) {
                    current.result + value
                } else {
                    value
                }
            } else {
                current.expression + value
            }

            val preview = calculatePreview(newExpression)

            current.copy(
                expression = newExpression,
                result = null,
                errorResId = null,
                isEvaluated = false,
                previewResult = preview
            )
        }
    }

    fun onBackspace() {
        _uiState.update { current ->
            if (current.expression.isNotEmpty()) {
                val newExpression = current.expression.dropLast(1)
                val preview = calculatePreview(newExpression)
                current.copy(
                    expression = newExpression,
                    result = null,
                    errorResId = null,
                    isEvaluated = false,
                    previewResult = preview
                )
            } else {
                current.copy(
                    errorResId = null,
                    result = null,
                    previewResult = null,
                    isEvaluated = false
                )
            }
        }
    }

    fun onClear() {
        _uiState.value = CalculatorUiState()
    }

    fun onCalculate() {
        val current = _uiState.value
        val expr = current.expression.trim()

        if (expr.isEmpty()) {
            return
        }

        try {
            val calcResult = Calculator.evaluate(expr)
            val formatted = Calculator.formatNumber(calcResult)
            _uiState.update {
                it.copy(
                    result = formatted,
                    previewResult = null,
                    errorResId = null,
                    isEvaluated = true
                )
            }
        } catch (e: ArithmeticException) {
            val errRes = if (e.message == "modulo_by_zero") {
                R.string.error_modulo_by_zero
            } else {
                R.string.error_division_by_zero
            }
            _uiState.update {
                it.copy(
                    result = null,
                    previewResult = null,
                    errorResId = errRes,
                    isEvaluated = false
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    result = null,
                    previewResult = null,
                    errorResId = R.string.error_invalid_expression,
                    isEvaluated = false
                )
            }
        }
    }

    private fun calculatePreview(expr: String): String? {
        if (expr.isBlank()) return null
        return try {
            val res = Calculator.evaluate(expr)
            Calculator.formatNumber(res)
        } catch (e: Exception) {
            null
        }
    }
}
