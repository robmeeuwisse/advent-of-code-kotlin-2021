package day02

import readInput

/**
 * [Day 2: Dive!](https://adventofcode.com/2021/day/2)
 */
fun main() {

    fun part1(input: List<String>): Int {
        return input.parseInstructions()
            .fold(Position()) { acc, instruction ->
                when (instruction.direction) {
                    "forward" -> acc.copy(horizontal = acc.horizontal + instruction.value)
                    "down" -> acc.copy(depth = acc.depth + instruction.value)
                    "up" -> acc.copy(depth = acc.depth - instruction.value)
                    else -> error("Unknown direction")
                }
            }.let { it.horizontal * it.depth }
    }

    fun part2(input: List<String>): Int {
        return input.parseInstructions()
            .fold(Position()) { acc, instruction ->
                when (instruction.direction) {
                    "forward" -> acc.copy(
                        horizontal = acc.horizontal + instruction.value,
                        depth = acc.depth + acc.aim * instruction.value,
                    )
                    "down" -> acc.copy(aim = acc.aim + instruction.value)
                    "up" -> acc.copy(aim = acc.aim - instruction.value)
                    else -> error("Unknown direction")
                }
            }.let { it.horizontal * it.depth }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("input_test.txt")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("input.txt")
    println(part1(input))
    println(part2(input))
}

private fun List<String>.parseInstructions() =
    map {
        Instruction(
            direction = it.substringBefore(' '),
            value = it.substringAfter(' ').toInt()
        )
    }

private data class Instruction(
    val direction: String,
    val value: Int,
)

private data class Position(
    val horizontal: Int = 0,
    val depth: Int = 0,
    val aim: Int = 0,
)
