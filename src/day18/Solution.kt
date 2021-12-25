@file:Suppress("TestFunctionName")

package day18

import org.junit.Assert.assertEquals
import org.junit.Test
import readInput
import kotlin.math.max

/**
 * [Day 18: Snailfish](https://adventofcode.com/2021/day/18)
 */
class Snailfish {

    @Test
    fun part1() {
        assertEquals(4140, part1(readInput("input_test.txt")))
        assertEquals(3734, part1(readInput("input.txt")))
    }

    @Test
    fun part2() {
        assertEquals(3993, part2(readInput("input_test.txt")))
        assertEquals(4837, part2(readInput("input.txt")))
    }
}

private fun part1(input: List<String>): Int {
    val result = input.map { Number(it) }.snailfishNumberSum().magnitude()
    println("Part 1 answer: $result")
    return result
}

private fun part2(input: List<String>): Int {
    val numbers = input.map { Number(it) }
    var maxMagnitude = 0
    for (number1 in numbers) {
        for (number2 in numbers - number1) {
            val magnitude = (number1 + number2).magnitude()
            maxMagnitude = max(magnitude, maxMagnitude)
        }
    }
    val result = maxMagnitude
    println("Part 2 answer: $result")
    return result
}

private sealed interface Number

private data class N(val value: Int) : Number {
    override fun toString(): String = value.toString()
}

private data class P(val left: Number, val right: Number) : Number {
    override fun toString(): String = "[$left,$right]"
}

private fun Number(value: String): Number {

    var start = 0

    fun parse(): Number {

        fun consumeChar(expect: Char) {
            val actual = value[start]
            check(actual == expect) { "Expected $expect but found $actual at $start in '$value'" }
            start += 1
        }

        fun parseRegular(): N {
            val end = (start..value.lastIndex).firstOrNull() { !value[it].isDigit() } ?: value.length
            val digits = value.substring(start, end)
            start = end
            return N(digits.toInt())
        }

        fun parsePair(): P {
            consumeChar('[')
            val left = parse()
            consumeChar(',')
            val right = parse()
            consumeChar(']')
            return P(left, right)
        }

        val next = value[start]
        return when {
            next == '[' -> parsePair()
            next.isDigit() -> parseRegular()
            else -> error("Expected pair or regular number but found $next at $start in '$value'")
        }
    }

    return parse()
}

private operator fun Number.plus(other: Number) = P(this, other).reduce()

private fun Number.explode(): Number {

    var fragmentLeft: Int? = null
    var fragmentRight: Int? = null

    fun absorbFirst(first: Number): Number =
        if (fragmentRight == null) first
        else when (first) {
            is N -> N(first.value + fragmentRight!!).also { fragmentRight = null }
            is P -> P(absorbFirst(first.left), first.right)
        }

    fun absorbLast(last: Number): Number =
        if (fragmentLeft == null) last
        else when (last) {
            is N -> N(last.value + fragmentLeft!!).also { fragmentLeft = null }
            is P -> P(last.left, absorbLast(last.right))
        }

    fun visit(number: Number, depth: Int = 1): Number {
        when (number) {
            is N -> return number
            is P -> {
                if (depth > 4 && number.left is N && number.right is N) {
                    fragmentLeft = number.left.value
                    fragmentRight = number.right.value
                    return N(0)
                } else {
                    val absorbed = P(absorbFirst(number.left), absorbLast(number.right))
                    if (absorbed != number) {
                        return absorbed
                    }
                    val newLeft = visit(number.left, depth + 1)
                    if (newLeft != number.left) {
                        return P(newLeft, absorbFirst(number.right))
                    }
                    val newRight = visit(number.right, depth + 1)
                    if (newRight != number.right) {
                        return P(absorbLast(number.left), newRight)
                    }
                    return number
                }
            }
        }
    }

    return visit(this)
}

private fun Number.split(): Number {

    fun visit(number: Number): Number {
        when (number) {
            is N -> {
                return if (number.value < 10)
                    number
                else
                    P(N(number.value / 2), N((number.value + 1) / 2))
            }
            is P -> {
                val newLeft = visit(number.left)
                if (newLeft != number.left)
                    return P(newLeft, number.right)
                val newRight = visit(number.right)
                if (newRight != number.right)
                    return P(number.left, newRight)
                return number
            }
        }
    }

    return visit(this)
}

private fun Number.reduce(): Number {
    var result = this
    while (true) {
        val previous = result
        result = previous.explode()
        if (result != previous) continue
        result = previous.split()
        if (result != previous) continue
        return result
    }
}

private fun List<Number>.snailfishNumberSum(): Number {
    return drop(1).fold(first()) { result, number -> result + number }
}

private fun Number.magnitude(): Int = when (this) {
    is N -> value
    is P -> 3 * left.magnitude() + 2 * right.magnitude()
}

class SnailfishTest {

    @Test
    fun `Parse Snailfish regular number`() {
        val actual = Number("10")
        val expected = N(10)
        assertEquals(expected, actual)
    }

    @Test
    fun `Parse Snailfish pair number`() {
        val actual = Number("[10,11]")
        val expected = P(N(10), N(11))
        assertEquals(expected, actual)
    }

    @Test
    fun `Snailfish addition`() {
        val actual = Number("[1,2]") + Number("[[3,4],5]")
        val expected = Number("[[1,2],[[3,4],5]]")
        assertEquals(expected, actual)
    }

    @Test
    fun `Snailfish number explode`() {
        assertEquals(
            Number("[[[[[9,8],1],2],3],4]").explode(),
            Number("[[[[0,9],2],3],4]"),
        )
        assertEquals(
            Number("[7,[6,[5,[4,[3,2]]]]]").explode(),
            Number("[7,[6,[5,[7,0]]]]"),
        )
        assertEquals(
            Number("[[6,[5,[4,[3,2]]]],1]").explode(),
            Number("[[6,[5,[7,0]]],3]"),
        )
        assertEquals(
            Number("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]").explode(),
            Number("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]"),
        )
        assertEquals(
            Number("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]").explode(),
            Number("[[3,[2,[8,0]]],[9,[5,[7,0]]]]"),
        )
    }

    @Test
    fun `Snailfish number split`() {
        assertEquals(
            Number("[[[[0,7],4],[15,[0,13]]],[1,1]]").split(),
            Number("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]"),
        )
        assertEquals(
            Number("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]").split(),
            Number("[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]"),
        )
    }

    @Test
    fun `Snailfish number reduce`() {
        assertEquals(
            Number("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]").reduce(),
            Number("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")
        )
    }

    @Test
    fun `Sum of Snailfish numbers example 1`() {
        val actual = listOf(
            "[1,1]",
            "[2,2]",
            "[3,3]",
            "[4,4]",
        ).map { Number(it) }.snailfishNumberSum()
        assertEquals(
            Number("[[[[1,1],[2,2]],[3,3]],[4,4]]"),
            actual
        )
    }

    @Test
    fun `Sum of Snailfish numbers example 2`() {
        val actual = listOf(
            "[1,1]",
            "[2,2]",
            "[3,3]",
            "[4,4]",
            "[5,5]",
        ).map { Number(it) }.snailfishNumberSum()
        assertEquals(
            Number("[[[[3,0],[5,3]],[4,4]],[5,5]]"),
            actual
        )
    }

    @Test
    fun `Sum of Snailfish numbers example 3`() {
        val actual = listOf(
            "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]",
            "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]",
            "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]",
            "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]",
            "[7,[5,[[3,8],[1,4]]]]",
            "[[2,[2,2]],[8,[8,1]]]",
            "[2,9]",
            "[1,[[[9,3],9],[[9,0],[0,7]]]]",
            "[[[5,[7,4]],7],1]",
            "[[[[4,2],2],6],[8,7]]",
        ).map { Number(it) }.snailfishNumberSum()
        assertEquals(
            Number("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"),
            actual
        )
    }

    @Test
    fun `Snailfish number magnitude`() {
        assertEquals(
            Number("[9,1]").magnitude(),
            29
        )
        assertEquals(
            Number("[[9,1],[1,9]]").magnitude(),
            129
        )
        assertEquals(
            Number("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]").magnitude(),
            3488
        )
    }
}
