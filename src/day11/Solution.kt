package day11

import org.junit.Assert.*
import org.junit.Test
import readInput

/**
 * [Day 11: Dumbo Octopus](https://adventofcode.com/2021/day/11)
 */
class DumboOctopus {

    @Test
    fun part1() {
        assertEquals(1656, part1(readInput("input_test.txt")))
        assertEquals(1571, part1(readInput("input.txt")))
    }

    @Test
    fun part2() {
        assertEquals(195, part2(readInput("input_test.txt")))
        assertEquals(387, part2(readInput("input.txt")))
    }
}

private fun part1(input: List<String>): Int {
    var grid = OctopusGrid(size = 10, digits = input.joinToString())
    repeat(100) { grid = grid.next() }

    val result = grid.flashCount
    println("Part 1 answer: $result")
    return result
}

private fun part2(input: List<String>): Int {
    var grid = OctopusGrid(size = 10, digits = input.joinToString())
    var round = 0
    do {
        round += 1
        println("Round $round")
        val lastFlashCount = grid.flashCount
        grid = grid.next()
        val nextFlashCount = grid.flashCount
    } while (nextFlashCount - lastFlashCount != 100)

    val result = round
    println("Part 2 answer: $result")
    return result
}

private class OctopusGrid(
    val size: Int = 10,
    var flashCount: Int = 0,
    private val values: MutableList<Int> = MutableList(size * size) { 0 },
) {
    constructor(size: Int, digits: String) :
            this(size, values = MutableList(size * size) { digits.filter { it.isDigit() }.get(it).digitToInt() })

    val rangeY = 0 until size
    val rangeX = 0 until size

    operator fun set(x: Int, y: Int, value: Int) {
        values[y * size + x] = value
    }

    operator fun get(x: Int, y: Int): Int =
        values[y * size + x]

    override fun toString(): String {
        val grid = this@OctopusGrid
        return rangeY.joinToString("\n") { y ->
            rangeX.joinToString("") { x ->
                grid[x, y].toString()
            }
        }
    }

    fun clone(): OctopusGrid = OctopusGrid(
        size = size,
        flashCount = flashCount,
        values = MutableList(size * size) { values[it] },
    )
}

private fun OctopusGrid.forNeighbours(x: Int, y: Int, block: (x: Int, y: Int) -> Unit) {
    if (x - 1 in rangeX && y - 1 in rangeY) block(x - 1, y - 1)
    if (x + 0 in rangeX && y - 1 in rangeY) block(x + 0, y - 1)
    if (x + 1 in rangeX && y - 1 in rangeY) block(x + 1, y - 1)
    if (x - 1 in rangeX && y + 0 in rangeY) block(x - 1, y + 0)
    if (x + 0 in rangeX && y + 0 in rangeY) block(x + 0, y + 0)
    if (x + 1 in rangeX && y + 0 in rangeY) block(x + 1, y + 0)
    if (x - 1 in rangeX && y + 1 in rangeY) block(x - 1, y + 1)
    if (x + 0 in rangeX && y + 1 in rangeY) block(x + 0, y + 1)
    if (x + 1 in rangeX && y + 1 in rangeY) block(x + 1, y + 1)
}

private fun OctopusGrid.next(): OctopusGrid {
    val result = clone()

    for (y in result.rangeY) {
        for (x in result.rangeX) {
            result[x, y] += 1
        }
    }

    val flashed = OctopusGrid(size)

    do {
        var extraFlashCount = 0
        val extraEnergy = OctopusGrid(size)
        for (y in extraEnergy.rangeY) {
            for (x in extraEnergy.rangeX) {
                if ((result[x, y] > 9) && (flashed[x, y] == 0)) {
                    extraEnergy.forNeighbours(x, y) { u, v -> extraEnergy[u, v] += 1 }

                    flashed[x, y] += 1
                    extraFlashCount += 1
                }
            }
        }

        for (y in result.rangeY) {
            for (x in result.rangeX) {
                result[x, y] += extraEnergy[x, y]
            }
        }
        result.flashCount += extraFlashCount
    } while (extraFlashCount > 0)

    for (y in rangeY) {
        for (x in rangeX) {
            result[x, y] = if (result[x, y] <= 9) result[x, y] else 0
        }
    }

    return result
}

class OctopusGridTests {
    @Test
    fun `Grid clone is immutable`() {
        val grid1 = OctopusGrid(1)
        val grid2 = grid1.clone()
        assertTrue(grid1 !== grid2)
        grid1[0, 0] = 1
        grid2[0, 0] = 2
        assertTrue(grid1[0, 0] != grid2[0, 0])
    }

    @Test
    fun `Step through increments and flashes, small grid`() {
        val initially = OctopusGrid(
            size = 5,
            digits = """
                11111
                19991
                19191
                19991
                11111
            """.trimIndent()
        )
        initially
            .also { println(it) }
            .next()
            .also { println(it) }
            .next()
            .also { println(it) }
    }

    @Test
    fun `Grid from and to string`() {
        val digits = """
            12
            34
        """.trimIndent()
        val grid = OctopusGrid(2, digits)
        val actual = grid.toString()
        assertEquals(digits, actual)
    }

    @Test
    fun `Step through increments and flashes, full grid`() {
        var step = OctopusGrid(
            size = 10,
            digits = """
                5483143223
                2745854711
                5264556173
                6141336146
                6357385478
                4167524645
                2176841721
                6882881134
                4846848554
                5283751526
            """.trimIndent()
        )

        step = step.next()
        val expected1 = """
                6594254334
                3856965822
                6375667284
                7252447257
                7468496589
                5278635756
                3287952832
                7993992245
                5957959665
                6394862637
            """.trimIndent()
        assertEquals(expected1, step.toString())

        step = step.next()
        val expected2 = """
                8807476555
                5089087054
                8597889608
                8485769600
                8700908800
                6600088989
                6800005943
                0000007456
                9000000876
                8700006848
            """.trimIndent()
        assertEquals(expected2, step.toString())

        step = step.next()
        val expected3 = """
                0050900866
                8500800575
                9900000039
                9700000041
                9935080063
                7712300000
                7911250009
                2211130000
                0421125000
                0021119000
            """.trimIndent()
        assertEquals(expected3, step.toString())

        step = step.next()
        val expected4 = """
                2263031977
                0923031697
                0032221150
                0041111163
                0076191174
                0053411122
                0042361120
                5532241122
                1532247211
                1132230211
            """.trimIndent()
        assertEquals(expected4, step.toString())

        step = step.next()
        val expected5 = """
                4484144000
                2044144000
                2253333493
                1152333274
                1187303285
                1164633233
                1153472231
                6643352233
                2643358322
                2243341322
            """.trimIndent()
        assertEquals(expected5, step.toString())

        step = step.next()
        val expected6 = """
                5595255111
                3155255222
                3364444605
                2263444496
                2298414396
                2275744344
                2264583342
                7754463344
                3754469433
                3354452433
            """.trimIndent()
        assertEquals(expected6, step.toString())

        repeat(4) { step = step.next() }
        val expected10 = """
                0481112976
                0031112009
                0041112504
                0081111406
                0099111306
                0093511233
                0442361130
                5532252350
                0532250600
                0032240000
            """.trimIndent()
        assertEquals(expected10, step.toString())

        repeat(10) { step = step.next() }
        val expected20 = """
                3936556452
                5686556806
                4496555690
                4448655580
                4456865570
                5680086577
                7000009896
                0000000344
                6000000364
                4600009543
            """.trimIndent()
        assertEquals(expected20, step.toString())

        repeat(80) { step = step.next() }
        val expected100 = """
                0397666866
                0749766918
                0053976933
                0004297822
                0004229892
                0053222877
                0532222966
                9322228966
                7922286866
                6789998766
            """.trimIndent()
        assertEquals(expected100, step.toString())
        println("Flash count: ${step.flashCount}")
    }
}
