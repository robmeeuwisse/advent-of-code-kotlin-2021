package day13

import org.junit.Assert.assertEquals
import org.junit.Test
import readInput

/**
 * [Day 13: Transparent Origami](https://adventofcode.com/2021/day/13)
 */
class TransparentOrigami {

    @Test
    fun part1() {
        assertEquals(17, part1(readInput("input_test.txt")))
        assertEquals(712, part1(readInput("input.txt")))
    }

    @Test
    fun part2() {
        val input = readInput("input.txt")
        var dots = input.parseDots()
        val folds = input.parseFolds()

        folds.forEach { fold ->
            dots = fold(dots)
        }

        println(dots.dump())
    }
}

private fun part1(input: List<String>): Int {
    var dots = input.parseDots()
    val folds = input.parseFolds()

    val fold = folds.first()
    dots = fold(dots)

    val result = dots.size
    println("Part 1 answer: $result")
    return result
}

private data class Dot(val x: Int, val y: Int)

private fun Set<Dot>.dump(): String = buildString {
    val maxX = this@dump.maxOf { it.x }
    val maxY = this@dump.maxOf { it.y }
    for (y in 0..maxY) {
        for (x in 0..maxX) {
            if (Dot(x, y) in this@dump) append("#") else append(".")
        }
        appendLine()
    }
}

private sealed interface Fold {
    operator fun invoke(dot: Dot): Dot
}

private data class FoldX(val fold: Int) : Fold {
    override fun invoke(dot: Dot): Dot =
        Dot(if (dot.x < fold) dot.x else 2 * fold - dot.x, dot.y)
}

private data class FoldY(val fold: Int) : Fold {
    override fun invoke(dot: Dot): Dot =
        Dot(dot.x, if (dot.y < fold) dot.y else 2 * fold - dot.y)
}

private operator fun Fold.invoke(dots: Set<Dot>): Set<Dot> =
    dots.map(this::invoke).toSet()

private fun List<String>.parseDots(): Set<Dot> =
    takeWhile { it.isNotBlank() }
        .map {
            Dot(
                x = it.substringBefore(",").toInt(),
                y = it.substringAfter(",").toInt(),
            )
        }
        .toSet()

private fun List<String>.parseFolds(): List<Fold> {
    return dropWhile { it.isNotBlank() }.drop(1).map {
        when {
            it.startsWith("fold along y") -> FoldY(it.substringAfter("=").toInt())
            it.startsWith("fold along x") -> FoldX(it.substringAfter("=").toInt())
            else -> error("Unexpected fold")
        }
    }
}

class TransparentOrigamiTest {

    @Test
    fun `Parse dots`() {
        val actual = readInput("input_test.txt").parseDots()
        assertEquals(18, actual.size)
    }

    @Test
    fun `Parse folds`() {
        val actual = readInput("input_test.txt").parseFolds()
        val expected = listOf(
            FoldY(7),
            FoldX(5)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Fold dot up`() {
        val fold = FoldY(10)
        val actual = fold(Dot(0, 13))
        val expected = Dot(0, 7)
        assertEquals(expected, actual)
    }

    @Test
    fun `Fold dots up and left`() {
        val dots = readInput("input_test.txt").parseDots()
        println(dots.dump())
        println()
        val actual = FoldX(5)(FoldY(7)(dots))
        println(actual.dump())
    }
}