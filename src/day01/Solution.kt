package day01

import readInput

/**
 * [Day 1: Sonar Sweep](https://adventofcode.com/2021/day/1)
 */
fun main() {
    fun part1(input: List<String>): Int {
        return input.map { it.toInt() }
            .zipWithNext()
            .count { it.second > it.first }
    }

    fun part2(input: List<String>): Int {
        return input.asSequence()
            .map { it.toInt() }
            .windowed(size = 3, step = 1)
            .map { it.sum() }
            .zipWithNext()
            .count { it.second > it.first }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("input_test.txt")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("input.txt")
    println(part1(input))
    println(part2(input))
}
