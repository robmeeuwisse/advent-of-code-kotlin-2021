package day04

import readInput

private const val BoardSize = 5

/**
 * [Day 4: Giant Squid](https://adventofcode.com/2021/day/4)
 */
fun main() {

    fun part1(input: List<String>): Int {
        val numbers = input.parseNumbers()
        val boards = input.parseBoards()

        numbers.forEach { number ->
            boards.forEachIndexed { boardIndex, board ->
                val hasNumber = board.mark(number)
                if (hasNumber && board.hasBingo()) {
                    val score = board.calcScore(number)
                    println("Bingo! on board #${boardIndex + 1} with score $score")
                    return score
                }
            }
        }
        error("Input should contain bingo")
    }

    fun part2(input: List<String>): Int {
        val numbers = input.parseNumbers()
        val boards = input.parseBoards()

        numbers.forEach { number ->
            boards.forEachIndexed { boardIndex, board ->
                val hasNumber = board.mark(number)
                if (hasNumber && boards.all { it.hasBingo() }) {
                    val score = board.calcScore(number)
                    println("Last Bingo! on board #${boardIndex + 1} with score $score")
                    return score
                }
            }
        }
        error("Input should contain bingo")
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("input_test.txt")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("input.txt")
    check(part1(input) == 89001)
    check(part2(input) == 7296)
}

private data class Board(
    val numbers: List<Int>,
) {
    private val marks = Array(BoardSize * BoardSize) { false }

    fun mark(number: Int): Boolean {
        val index = numbers.indexOf(number)
        val hasNumber = index >= 0
        if (hasNumber) {
            marks[index] = true
        }
        return hasNumber
    }

    fun hasBingo(): Boolean =
        (0 until BoardSize).any { isBingoRow(it) || isBingoColumn(it) }

    fun calcScore(lastNumber: Int): Int =
        lastNumber * numbers.filterIndexed { index, _ -> !marks[index] }.sum()

    private fun isBingoRow(row: Int): Boolean =
        (row * BoardSize until (row + 1) * BoardSize).all { marks[it] }

    private fun isBingoColumn(column: Int): Boolean =
        (0 until BoardSize * BoardSize step BoardSize).all { marks[it + column] }
}

private fun List<String>.parseNumbers(): List<Int> =
    first().split(",").map { it.toInt() }

private fun List<String>.parseBoards(): List<Board> {
    val source = iterator()
    source.next() // skip line of drawn numbers
    return buildList {
        source.asBoards()
            .forEach { board -> add(board) }
    }
}

private fun Iterator<String>.asBoards(): Iterator<Board> {
    val source: Iterator<String> = this
    return object : Iterator<Board> {

        override fun hasNext(): Boolean =
            source.hasNext()

        override fun next(): Board {
            source.next() // skip blank separator line
            val numbers = buildList {
                repeat(BoardSize) {
                    addAll(
                        source.next()
                            .chunked(3)
                            .map { it.trim().toInt() }
                    )
                }
            }
            return Board(numbers)
        }
    }
}
