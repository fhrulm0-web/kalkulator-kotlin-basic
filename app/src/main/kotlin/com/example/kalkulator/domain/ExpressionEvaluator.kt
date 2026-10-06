package com.example.kalkulator.domain

class ExpressionEvaluator {

    fun evaluate(expression: String): Double {
        if (expression.isBlank()) {
            throw IllegalArgumentException("Expression cannot be blank")
        }
        val parser = Parser(expression)
        val result = parser.parse()
        if (result.isNaN() || result.isInfinite()) {
            if (result.isInfinite()) {
                throw ArithmeticException("division_by_zero")
            }
        }
        return result
    }

    private class Parser(private val input: String) {
        private var pos: Int = 0

        fun parse(): Double {
            val result = parseExpression()
            skipWhitespace()
            if (pos < input.length) {
                throw IllegalArgumentException("Unexpected character '${input[pos]}' at index $pos")
            }
            return result
        }

        private fun skipWhitespace() {
            while (pos < input.length && input[pos].isWhitespace()) {
                pos++
            }
        }

        private fun peek(): Char? {
            skipWhitespace()
            return if (pos < input.length) input[pos] else null
        }

        private fun consume(): Char {
            skipWhitespace()
            if (pos >= input.length) {
                throw IllegalArgumentException("Unexpected end of expression")
            }
            return input[pos++]
        }

        // expression = term (('+' | '-') term)*
        private fun parseExpression(): Double {
            var left = parseTerm()
            while (true) {
                val next = peek()
                if (next == '+' || next == '-') {
                    consume()
                    val right = parseTerm()
                    left = if (next == '+') left + right else left - right
                } else {
                    break
                }
            }
            return left
        }

        // term = factor (('*' | '/' | '%') factor)*
        private fun parseTerm(): Double {
            var left = parseFactor()
            while (true) {
                val next = peek()
                if (next == '*' || next == '×' || next == '/' || next == '÷' || next == '%') {
                    consume()
                    val right = parseFactor()
                    when (next) {
                        '*', '×' -> left *= right
                        '/', '÷' -> {
                            if (right == 0.0) throw ArithmeticException("division_by_zero")
                            left /= right
                        }
                        '%' -> {
                            if (right == 0.0) throw ArithmeticException("modulo_by_zero")
                            left %= right
                        }
                    }
                } else {
                    break
                }
            }
            return left
        }

        // factor = ('+' | '-') factor | primary
        private fun parseFactor(): Double {
            val next = peek()
            if (next == '+') {
                consume()
                return parseFactor()
            }
            if (next == '-') {
                consume()
                return -parseFactor()
            }
            return parsePrimary()
        }

        // primary = NUMBER | '(' expression ')'
        private fun parsePrimary(): Double {
            val next = peek() ?: throw IllegalArgumentException("Unexpected end of expression at index $pos")

            if (next == '(') {
                consume() // consume '('
                val result = parseExpression()
                val closing = peek()
                if (closing != ')') {
                    throw IllegalArgumentException("Missing closing parenthesis ')' at index $pos")
                }
                consume() // consume ')'
                return result
            }

            return parseNumber()
        }

        private fun parseNumber(): Double {
            skipWhitespace()
            val start = pos
            var hasDot = false

            while (pos < input.length) {
                val c = input[pos]
                if (c.isDigit()) {
                    pos++
                } else if (c == '.' && !hasDot) {
                    hasDot = true
                    pos++
                } else {
                    break
                }
            }

            if (start == pos) {
                val unexpected = if (pos < input.length) "'${input[pos]}'" else "end of input"
                throw IllegalArgumentException("Expected number at index $pos, found $unexpected")
            }

            val numberStr = input.substring(start, pos)
            return numberStr.toDoubleOrNull()
                ?: throw IllegalArgumentException("Invalid number format '$numberStr' at index $start")
        }
    }
}
