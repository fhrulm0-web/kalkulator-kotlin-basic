package com.example.kalkulator.domain

import java.math.BigDecimal

enum class Operation(val symbol: String) {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%")
}

object Calculator {
    fun calculate(first: Double, second: Double, op: Operation): Double {
        return when (op) {
            Operation.ADD -> first + second
            Operation.SUBTRACT -> first - second
            Operation.MULTIPLY -> first * second
            Operation.DIVIDE -> {
                if (second == 0.0) throw ArithmeticException("division_by_zero")
                first / second
            }
            Operation.MODULO -> {
                if (second == 0.0) throw ArithmeticException("modulo_by_zero")
                first % second
            }
        }
    }

    fun parseOperation(symbol: String): Operation? {
        return Operation.values().firstOrNull { it.symbol == symbol.trim() }
    }

    fun formatNumber(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return value.toString()
        if (value == 0.0 || value == -0.0) return "0"

        return try {
            val bd = BigDecimal.valueOf(value)
            if (bd.compareTo(BigDecimal.ZERO) == 0) {
                "0"
            } else {
                val plain = bd.stripTrailingZeros().toPlainString()
                if (plain.endsWith('.')) plain.dropLast(1) else plain
            }
        } catch (e: Exception) {
            value.toBigDecimal().toPlainString()
        }
    }

    fun evaluate(expression: String): Double {
        return ExpressionEvaluator().evaluate(expression)
    }
}
