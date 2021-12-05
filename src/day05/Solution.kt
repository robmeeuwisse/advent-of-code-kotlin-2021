package day05

import readInput
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * [Day 5: Hydrothermal Venture](https://adventofcode.com/2021/day/5)
 */
fun main() {

    fun part1(input: List<String>): Int {
        val clouds = input.parseClouds()
            .filter { it.deltaX == 0 || it.deltaY == 0 }

        val coverage = mutableMapOf<Point, Int>()
        clouds.flatMap { it.points() }.forEach {
            coverage[it] = coverage.getOrDefault(it, 0) + 1
        }

        coverage.print("Cloud coverage")

        val result = coverage.count { it.value > 1 }
        println("Part 1 answer: $result")
        return result
    }

    fun part2(input: List<String>): Int {
        val clouds = input.parseClouds()

        val coverage = mutableMapOf<Point, Int>()
        clouds.flatMap { it.points() }.forEach {
            coverage[it] = coverage.getOrDefault(it, 0) + 1
        }

        coverage.print("Cloud coverage")

        val result = coverage.count { it.value > 1 }
        println("Part 2 answer: $result")
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("input_test.txt")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("input.txt")
    check(part1(input) == 5197)
    check(part2(input) == 18605)
}

private data class Point(
    val x: Int,
    val y: Int,
)

private data class Cloud(
    val start: Point,
    val end: Point,
) {
    val deltaX: Int get() = end.x - start.x
    val deltaY: Int get() = end.y - start.y

    fun points(): List<Point> {
        val steps = max(abs(deltaX), abs(deltaY))
        return (0..steps).map {
            val fraction = it.toDouble() / steps.toDouble()
            Point(
                x = start.x + (fraction * deltaX).roundToInt(),
                y = start.y + (fraction * deltaY).roundToInt(),
            )
        }
    }
}

private fun List<String>.parseClouds(): List<Cloud> =
    map { it.parseCloud() }

private fun String.parseCloud(): Cloud {
    val (start, end) = split(" -> ")
    return Cloud(
        start.parsePoint(),
        end.parsePoint(),
    )
}

private fun String.parsePoint(): Point {
    val (x, y) = split(",")
    return Point(x.toInt(), y.toInt())
}

private fun Map<Point, Int>.print(header: String) {
    println(header)
    repeat(this.maxOf { it.key.x } + 1) { y ->
        repeat(this.maxOf { it.key.y } + 1) { x ->
            print(get(Point(x, y)) ?: ".")
        }
        println()
    }
    println()
}
