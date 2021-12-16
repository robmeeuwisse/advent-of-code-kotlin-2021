package day14

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import readInput

/**
 * [Day 14: Extended Polymerization](https://adventofcode.com/2021/day/14)
 */
class ExtendedPolymerization {

    @Test
    fun part1() {
        assertEquals(1588, part1(readInput("input_test.txt")))
        assertEquals(2768, part1(readInput("input.txt")))
    }

    @Test
    fun part2() {
        assertEquals(2188189693529, part2(readInput("input_test.txt")))
        assertEquals(2914365137499, part2(readInput("input.txt")))
    }
}

private fun part1(input: List<String>): Long {
    val template = input.parseTemplate()
    val rules = input.parseRules()

    var polymer = template.toPolymer()
    repeat(10) {
        polymer = polymer.insert(rules)
    }

    val result = polymer.toElementCounts().answer()
    println("Part 1 answer: $result")
    return result
}

private fun part2(input: List<String>): Long {
    val template = input.parseTemplate()
    val rules = input.parseRules()

    var polymer = template.toPolymer()
    repeat(40) {
        polymer = polymer.insert(rules)
    }

    val result = polymer.toElementCounts().answer()
    println("Part 2 answer: $result")
    return result
}

private fun List<String>.parseTemplate(): String = first()

private fun List<String>.parseRules(): List<Rule> =
    drop(2).map {
        Rule(
            pair = it.substringBefore(" -> "),
            char = it.substringAfter(" -> ").single(),
        )
    }

data class Rule(val pair: String, val char: Char) {
    init {
        check(pair.length == 2) { "Pair should be two long" }
    }

    override fun toString(): String = "$pair -> $char"
}

private typealias Polymer = Map<String, Long>

private fun String.toPolymer(): Polymer {
    val result = mutableMapOf<String, Long>()
    windowed(2, partialWindows = true).forEach { pair ->
        result[pair] = result.getOrDefault(pair, 0) + 1
    }
    return result.toSortedMap()
}

private fun Polymer.insert(rules: List<Rule>): Polymer {
    val source = this@insert
    val result = mutableMapOf<String, Long>()
    result.putAll(source)
    rules.forEach { insertion ->
        val sourceEntry = source.entries.find { it.key == insertion.pair }
        if (sourceEntry != null) {
            println("Inserting $insertion")

            val pair = sourceEntry.key
            val count = sourceEntry.value
            result[pair] = result.getOrDefault(pair, 0) - count
            println("$pair - $count = ${result[pair]}")

            val pairLeft: String = insertion.pair.substring(0, 1) + insertion.char
            result[pairLeft] = result.getOrDefault(pairLeft, 0) + count
            println("$pairLeft + $count = ${result[pairLeft]}")

            val pairRight: String = insertion.char + insertion.pair.substring(1)
            result[pairRight] = result.getOrDefault(pairRight, 0) + count
            println("$pairRight + $count = ${result[pairRight]}")
        }
    }
    return result.filter { it.value > 0 }.toSortedMap()
}

fun Polymer.toElementCounts(): Map<Char, Long> =
    entries.groupBy { pair -> pair.key.first() }
        .map { element -> element.key to element.value.sumOf { pair -> pair.value } }
        .toMap()

fun Map<Char, Long>.answer(): Long {
    val mostCommon = maxOf { it.value }
    val leastCommon = minOf { it.value }
    return mostCommon - leastCommon
}

class ExtendedPolymerizationTests {

    @Test
    fun `Input does not have duplicate pairs`() {
        val input = readInput("input.txt")
        val rules = input.parseRules()
        val duplicates = rules.groupingBy { it.pair }.eachCount().filter { it.value > 1 }
        assertTrue(duplicates.isEmpty())
    }

    @Test
    fun `Test input insert samples`() {
        val template = "NNCB"
        val rules = readInput("input_test.txt").parseRules()
        var actual = template.toPolymer()

        actual = actual.insert(rules)
        assertEquals("NCNBCHB".toPolymer(), actual)
        actual = actual.insert(rules)
        assertEquals("NBCCNBBBCBHCB".toPolymer(), actual)
        actual = actual.insert(rules)
        assertEquals("NBBBCNCCNBBNBNBBCHBHHBCHB".toPolymer(), actual)
        actual = actual.insert(rules)
        assertEquals("NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB".toPolymer(), actual)

        actual = actual.insert(rules)
        assertEquals(97, actual.values.sum())

        repeat(5) { actual = actual.insert(rules) }
        assertEquals(3073, actual.values.sum())

        val elementCounts = actual.toElementCounts()
        assertEquals(1749L, elementCounts['B'])
        assertEquals(298L, elementCounts['C'])
        assertEquals(161L, elementCounts['H'])
        assertEquals(865L, elementCounts['N'])

        assertEquals(1588, elementCounts.answer())
    }
}
