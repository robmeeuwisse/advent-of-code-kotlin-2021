package day06

import readInput

/**
 * [Day 6: Lanternfish](https://adventofcode.com/2021/day/6)
 */
fun main() {

    fun part1(input: List<String>): Long {
        val result = solve(input, 80)
        println("Part 1 answer: $result")
        return result
    }

    fun part2(input: List<String>): Long {
        val result = solve(input, 256)
        println("Part 2 answer: $result")
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("input_test.txt")
    check(part1(testInput) == 5934L)
    check(part2(testInput) == 26984457539)

    val input = readInput("input.txt")
    check(part1(input) == 361169L)
    check(part2(input) == 1634946868992)
}

private fun solve(input: List<String>, maxDays: Int): Long {
    var fish: Map<Int, Long> = input.first().split(",").map { it.toInt() }
        .groupBy { it }
        .mapValues { cohort ->
            cohort.value.size.toLong()
        }
        .toMutableMap()
    println("initial: ${fish.toCohortsString()}")
    repeat(maxDays) { day ->
        val next = mutableMapOf<Int, Long>()
        fish.forEach { cohort ->
            if (cohort.key == 0) {
                next[6] = next.getOrDefault(6, 0) + cohort.value
                next[8] = next.getOrDefault(8, 0) + cohort.value
            } else {
                next[cohort.key - 1] = next.getOrDefault(cohort.key - 1, 0) + cohort.value
            }
        }
        fish = next
        println("After ${day + 1} days: ${fish.toCohortsString()}")
    }

    return fish.values.sum()
}

private fun Map<Int, Long>.toCohortsString(): String {
    val cohorts = keys.sorted().joinToString { "$it=${get(it)}" }
    val total = values.sum()
    return "cohorts: $cohorts; total: $total"
}
