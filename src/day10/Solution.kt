package day10

import org.junit.Assert.assertEquals
import org.junit.Test
import readInput

/**
 * [Day 10: Syntax Scoring](https://adventofcode.com/2021/day/10)
 */
class SyntaxScoring {

    @Test
    fun part1() {
        assertEquals(26397, part1(readInput("input_test.txt")))
        assertEquals(394647, part1(readInput("input.txt")))
    }

    @Test
    fun part2() {
        assertEquals(288957, part2(readInput("input_test.txt")))
        assertEquals(2380061249, part2(readInput("input.txt")))
    }

    @Test
    fun completionString() {
        assertEquals("}}]])})]", "[({(<(())[]>[[{[]{<()<>>".removeValidChunks().completionString())
        assertEquals(")}>]})", "[(()[<>])]({[<{<<[]>>(".removeValidChunks().completionString())
        assertEquals("}}>}>))))", "(((({<>}<{<{<>}{[]{[]{}".removeValidChunks().completionString())
        assertEquals("]]}}]}]}>", "{<[[]]>}<{[{[{[]{()[[[]".removeValidChunks().completionString())
        assertEquals("])}>", "<{([{{}}[<[[[<>{}]]]>[]]".removeValidChunks().completionString())
    }

    @Test
    fun autocompleteScore() {
        assertEquals(288957, "}}]])})]".autocompleteScore())
        assertEquals(5566, ")}>]})".autocompleteScore())
        assertEquals(1480781, "}}>}>))))".autocompleteScore())
        assertEquals(995444, "]]}}]}]}>".autocompleteScore())
        assertEquals(294, "])}>".autocompleteScore())
    }
}

fun part1(input: List<String>): Int {
    return input.asSequence()
        .map { it.removeValidChunks() }
        .filter { it.isCorrupted() }
        .map { corrupted ->
            val illegalClosingChar = corrupted.first { it.isClosingBracket }
            illegalClosingChar.syntaxCheckScore
        }.sum()
}

fun part2(input: List<String>): Long {
    val scores = input
        .map { it.removeValidChunks() }
        .filter { it.isIncomplete() }
        .map {
            val completionChars = it.completionString()
            completionChars.autocompleteScore()
        }
        .sorted()
    return scores[scores.size / 2]
}

enum class Bracket(
    val openingChar: Char,
    val closingChar: Char,
) {
    Square('[', ']'),
    Round('(', ')'),
    Curly('{', '}'),
    Angle('<', '>')
}

val Char.isOpeningBracket: Boolean get() = Bracket.values().any { it.openingChar == this }
val Char.isClosingBracket: Boolean get() = Bracket.values().any { it.closingChar == this }
fun Char.toBracket(): Bracket = Bracket.values().first { it.openingChar == this }

private val Char.syntaxCheckScore: Int
    get() = when (this) {
        ')' -> 3
        ']' -> 57
        '}' -> 1197
        '>' -> 25137
        else -> error("Not a chunk closing: $this")
    }

private val Char.autocompleteScore: Int
    get() = when (this) {
        ')' -> 1
        ']' -> 2
        '}' -> 3
        '>' -> 4
        else -> error("Not a chunk closing: $this")
    }

private fun String.autocompleteScore(): Long =
    fold(0L) { acc: Long, char -> acc * 5L + char.autocompleteScore }

fun String.removeValidChunks(): String {
    var result = this
    while (true) {
        val next = result.removeEmptyChunk()
        if (next == result) break
        result = next
    }
    return result
}

fun String.removeEmptyChunk(): String {
    val index = indexOfFirst { it.isClosingBracket }
    if (index < 1) return this
    val isEmptyChunk = Bracket.values().any { it.openingChar == get(index - 1) && it.closingChar == get(index) }
    return if (isEmptyChunk) removeRange(index - 1..index) else this
}

fun String.isCorrupted(): Boolean =
    removeValidChunks().let { remaining -> remaining.isNotEmpty() && remaining.any { it.isClosingBracket } }

fun String.isIncomplete(): Boolean =
    removeValidChunks().let { remaining -> remaining.isNotEmpty() && remaining.all { it.isOpeningBracket } }

fun String.completionString(): String =
    reversed().map { it.toBracket().closingChar }.joinToString("")
