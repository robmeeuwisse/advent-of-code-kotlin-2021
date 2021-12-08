package day08

import readInput

private const val Digit1SegmentCount = 2
private const val Digit4SegmentCount = 4
private const val Digit7SegmentCount = 3
private const val Digit8SegmentCount = 7

private val EasyDigitSegmentCounts = listOf(
    Digit1SegmentCount,
    Digit4SegmentCount,
    Digit7SegmentCount,
    Digit8SegmentCount,
)

/**
 * [Day 8: Seven Segment Search](https://adventofcode.com/2021/day/8)
 */
fun main() {

    fun part1(input: List<String>): Int {
        val entries = input.parseNotebookEntries()

        val easyDigitsCount = entries.sumOf { entry ->
            entry.outputs.count { digit ->
                digit.length in EasyDigitSegmentCounts
            }
        }

        val result = easyDigitsCount
        println("Part 1 answer: $result")
        return result
    }

    fun part2(input: List<String>): Int {
        val entries = input.parseNotebookEntries()
        val result = entries.sumOf { decodeOutputValue(it) }
        println("Part 2 answer: $result")
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("input_test.txt")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("input.txt")
    check(part1(input) == 548)
    check(part2(input) == 1074888)
}

private data class NotebookEntry(
    val patterns: List<String>,
    val outputs: List<String>,
)

private fun List<String>.parseNotebookEntries() = map { line ->
    val (patterns, digits) = line.split(" | ")
    NotebookEntry(
        patterns = patterns.split(" "),
        outputs = digits.split(" "),
    )
}

//   0:      1:      2:      3:      4:     5:      6:      7:      8:      9:
//  aaaa    ....    aaaa    aaaa    ....    aaaa    aaaa    aaaa    aaaa    aaaa
// b    c  .    c  .    c  .    c  b    c  b    .  b    .  .    c  b    c  b    c
// b    c  .    c  .    c  .    c  b    c  b    .  b    .  .    c  b    c  b    c
//  ....    ....    dddd    dddd    dddd    dddd    dddd    ....    dddd    dddd
// e    f  .    f  e    .  .    f  .    f  .    f  e    f  .    f  e    f  .    f
// e    f  .    f  e    .  .    f  .    f  .    f  e    f  .    f  e    f  .    f
//  gggg    ....    gggg    gggg    ....    gggg    gggg    ....    gggg    gggg

private const val segmentFrequencyOfB = 6
private const val segmentFrequencyOfE = 4
private const val segmentFrequencyOfF = 9

private fun decodeOutputValue(entry: NotebookEntry): Int {

    val wireFrequencies: Map<Char, Int> = entry.patterns
        .flatMap { it.toSet() }
        .groupingBy { it }
        .eachCount()

    val wiresOf1 = entry.patterns.first { it.length == Digit1SegmentCount }.toSet()
    val wiresOf4 = entry.patterns.first { it.length == Digit4SegmentCount }.toSet()
    val wiresOf7 = entry.patterns.first { it.length == Digit7SegmentCount }.toSet()
    val wiresOf8 = entry.patterns.first { it.length == Digit8SegmentCount }.toSet()

    val wireOfA = (wiresOf7 - wiresOf1).single()
    val wireOfB = wireFrequencies.keys.single { wireFrequencies[it] == segmentFrequencyOfB }
    val wireOfE = wireFrequencies.keys.single { wireFrequencies[it] == segmentFrequencyOfE }
    val wireOfF = wireFrequencies.keys.single { wireFrequencies[it] == segmentFrequencyOfF }
    val wireOfC = (wiresOf1 - wireOfF).single()
    val wireOfD = (wiresOf4 - wireOfB - wireOfC - wireOfF).single()
    val wireOfG = (wiresOf8 - wireOfA - wireOfB - wireOfC - wireOfD - wireOfE - wireOfF).single()

    val wiresOf0 = setOf(wireOfA, wireOfB, wireOfC, wireOfE, wireOfF, wireOfG)
    val wiresOf2 = setOf(wireOfA, wireOfC, wireOfD, wireOfE, wireOfG)
    val wiresOf3 = setOf(wireOfA, wireOfC, wireOfD, wireOfF, wireOfG)
    val wiresOf5 = setOf(wireOfA, wireOfB, wireOfD, wireOfF, wireOfG)
    val wiresOf6 = setOf(wireOfA, wireOfB, wireOfD, wireOfE, wireOfF, wireOfG)
    val wiresOf9 = setOf(wireOfA, wireOfB, wireOfC, wireOfD, wireOfF, wireOfG)

    val digitByWires = mapOf<Set<Char>, Int>(
        wiresOf0 to 0,
        wiresOf1 to 1,
        wiresOf2 to 2,
        wiresOf3 to 3,
        wiresOf4 to 4,
        wiresOf5 to 5,
        wiresOf6 to 6,
        wiresOf7 to 7,
        wiresOf8 to 8,
        wiresOf9 to 9,
    )

    val digits = entry.outputs.map { output ->
        checkNotNull(digitByWires[output.toSet()])
    }
    return digits.fold(0) { result, digit -> 10 * result + digit }
}
