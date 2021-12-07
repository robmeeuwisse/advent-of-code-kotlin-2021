package day07

import readInput
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * [Day 7: The Treachery of Whales](https://adventofcode.com/2021/day/7)
 */
fun main() {

    fun part1(input: List<String>): Int {
        val positions = input.parsePositions()

        fun fuelCost(start: Int, destination: Int) =
            abs(start - destination)

        val destination = positions.median()

        positions.forEach { position ->
            println("- Move from $position to $destination: ${fuelCost(position, destination)}")
        }

        val totalFuel = positions.sumOf { fuelCost(it, destination) }
        println("Part 1 answer: $totalFuel total fuel aligning at $destination")
        return totalFuel
    }

    fun part2(input: List<String>): Int {
        val positions = input.parsePositions()

        fun fuelCost(start: Int, destination: Int): Int {
            val d = abs(start - destination)
            return d * (d + 1) / 2
        }

        fun totalFuelCost(destination: Int) =
            positions.sumOf { fuelCost(it, destination) }

        val allPositions = IntRange(
            start = positions.minOf { it },
            endInclusive = positions.maxOf { it },
        )
        val destination = checkNotNull(allPositions.minByOrNull { totalFuelCost(it) })

        positions.sorted().forEach { position ->
            println("- Move from $position to $destination: ${fuelCost(position, destination)}")
        }

        val totalFuel = totalFuelCost(destination)
        println("Part 2 answer: $totalFuel total fuel aligning at $destination")
        return totalFuel
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("input_test.txt")
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInput("input.txt")
    check(part1(input) == 359648)
    check(part2(input) == 100727924)
}

private fun List<String>.parsePositions(): List<Int> =
    first().split(",").map { it.toInt() }

private fun List<Int>.median(): Int {
    val sorted = sorted()
    val median1 = sorted[lastIndex / 2]
    val median2 = sorted[lastIndex / 2 + lastIndex % 2]
    return (median1 + median2) / 2
}
