package day09

import readInput

/**
 * [Day 9: Smoke Basin](https://adventofcode.com/2021/day/9)
 */
fun main() {

    fun part1(input: List<String>): Int {
        val heightMap = input.parseHeightMap()

        val riskLevels = heightMap.lowPoints()
            .also { println(it.joinToString("\n") { "Low point at $it" }) }
            .map { p -> 1 + heightMap.height(p) }

        val result = riskLevels.sum()
        println("Part 1 answer: $result")
        return result
    }

    fun part2(input: List<String>): Int {
        val heightMap = input.parseHeightMap()

        val result = heightMap.lowPoints()
            .map { p ->
                val size = heightMap.basinSize(mutableSetOf(p))
                println("Basin at $p, size $size")
                size
            }
            .sortedDescending()
            .take(3)
            .fold(1) { result, size -> result * size }

        println("Part 2 answer: $result")
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("input_test.txt")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("input.txt")
    check(part1(input) == 475)
    check(part2(input) == 1092012)
}

private data class Point(val x: Int, val y: Int) {
    override fun toString(): String = "($x, $y)"
}
private typealias HeightMap = List<List<Int>>

private fun List<String>.parseHeightMap(): HeightMap =
    map { it.toList().map(Char::digitToInt) }

private val HeightMap.rangeX: IntRange get() = IntRange(0, first().lastIndex)
private val HeightMap.rangeY: IntRange get() = IntRange(0, lastIndex)

private fun HeightMap.height(p: Point): Int = this[p.y][p.x]

private fun HeightMap.forEachPoint(block: (Point) -> Unit) {
    rangeY.forEach { y ->
        rangeX.forEach { x ->
            block(Point(x, y))
        }
    }
}

private fun HeightMap.lowPoints(): Set<Point> {
    return buildSet {
        forEachPoint { p ->
            val lowestNeighbourHeight = neighbours(p).minOf { height(it) }
            if (height(p) < lowestNeighbourHeight) {
                add(p)
            }
        }
    }
}

private operator fun HeightMap.contains(p: Point): Boolean =
    p.x in rangeX && p.y in rangeY

private fun HeightMap.neighbours(p: Point): Set<Point> = setOfNotNull(
    Point(p.x, p.y - 1).takeIf { it in this },
    Point(p.x, p.y + 1).takeIf { it in this },
    Point(p.x - 1, p.y).takeIf { it in this },
    Point(p.x + 1, p.y).takeIf { it in this },
)

private fun HeightMap.basinSize(basin: MutableSet<Point>): Int {
    val size = basin.size
    basin += basin.flatMap { neighbours(it) }
        .filter { height(it) < 9 }
        .toSet()
    return if (basin.size == size) size else basinSize(basin)
}
