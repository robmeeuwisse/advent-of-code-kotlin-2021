package day03

import readInput

/**
 * [Day 3: Binary Diagnostic](https://adventofcode.com/2021/day/3)
 */
fun main() {

    fun part1(input: List<String>): Int {
        val inputWidth = input.first().length

        val gammaBits = (0 until inputWidth).map { position ->
            input.histogramAt(position).mostCommonBit()
        }
        val gammaRate = gammaBits.joinToString("").toInt(2)

        val epsilonMask = (1 shl inputWidth) - 1
        val epsilonRate = gammaRate.xor(epsilonMask)

        return gammaRate * epsilonRate
    }

    fun part2(input: List<String>): Int {
        val o2Rate = oxygenGeneratorBits(input).joinToString("").toInt(2)
        val co2Rate = co2ScrubberBits(input).joinToString("").toInt(2)
        return o2Rate * co2Rate
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("input_test.txt")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("input.txt")
    println(part1(input).also { check(it == 3882564) })
    println(part2(input).also { check(it == 3385170) })
}

fun List<String>.histogramAt(position: Int): Map<Char, Int> {
    val reportWidth = first().length
    return joinToString(separator = "")
        .filterIndexed { index, _ -> index.rem(reportWidth) == position }
        .groupBy { it }
        .map { it.key to it.value.size }
        .toMap()
}

fun Map<Char, Int>.mostCommonBit(prefer: Char = '\u0000'): Char {
    val zeroCount = getOrDefault('0', 0)
    val oneCount = getOrDefault('1', 0)
    return when {
        zeroCount > oneCount -> '0'
        zeroCount < oneCount -> '1'
        else -> prefer
    }
}

fun Map<Char, Int>.leastCommonBit(prefer: Char): Char {
    val zeroCount = getOrDefault('0', 0)
    val oneCount = getOrDefault('1', 0)
    return when {
        zeroCount < oneCount -> '0'
        zeroCount > oneCount -> '1'
        else -> prefer
    }
}

fun oxygenGeneratorBits(input: List<String>, position: Int = 0): List<String> {
    if (input.size == 1) return input
    val mostCommon = input.histogramAt(position).mostCommonBit(prefer = '1')
    val filteredInput = input.filter { it[position] == mostCommon }
    return oxygenGeneratorBits(filteredInput, position + 1)
}

fun co2ScrubberBits(input: List<String>, position: Int = 0): List<String> {
    if (input.size == 1) return input
    val leastCommon = input.histogramAt(position).leastCommonBit(prefer = '0')
    val filteredInput = input.filter { it[position] == leastCommon }
    return co2ScrubberBits(filteredInput, position + 1)
}
