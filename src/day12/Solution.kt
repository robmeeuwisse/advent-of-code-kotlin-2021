package day12

import org.junit.Assert.*
import org.junit.Test
import readInput

/**
 * [Day 12: Passage Pathing](https://adventofcode.com/2021/day/12)
 */
class PassagePathing {

    @Test
    fun part1() {
        assertEquals(10, part1(readInput("input_test_1.txt")))
        assertEquals(19, part1(readInput("input_test_2.txt")))
        assertEquals(226, part1(readInput("input_test_3.txt")))
        assertEquals(4773, part1(readInput("input.txt")))
    }

    @Test
    fun part2() {
        assertEquals(36, part2(readInput("input_test_1.txt")))
        assertEquals(103, part2(readInput("input_test_2.txt")))
        assertEquals(3509, part2(readInput("input_test_3.txt")))
        assertEquals(116985, part2(readInput("input.txt")))
    }
}

private fun part1(input: List<String>): Int {
    val caveSystem = parseInput(input)
    var paths = listOf(Path("start"))
    do {
        val previous = paths
        paths = caveSystem.findPaths(paths, "end") { cave, path ->
            !(cave.isSmall && cave in path)
        }
    } while (paths != previous)
    println(paths.sortedBy { it.toString() }.joinToString("\n"))

    val result = paths.size
    println("Part 1 answer: $result")
    return result
}

private fun part2(input: List<String>): Int {
    val caveSystem = parseInput(input)
    var paths = listOf(Path("start"))
    do {
        val previous = paths
        paths = caveSystem.findPaths(paths, "end") { cave, path ->
            cave != "start" &&
                    !(cave.isSmall && cave in path && path.isAnySmallCaveVisitedTwice())
        }
    } while (paths != previous)
    println(paths.sortedBy { it.toString() }.joinToString("\n"))

    val result = paths.size
    println("Part 2 answer: $result")
    return result
}

private fun parseInput(input: List<String>): CaveSystem {
    val caves = mutableSetOf<Cave>()
    val corridors = mutableSetOf<Corridor>()
    input.forEach { line ->
        val n1 = line.substringBefore("-")
        val n2 = line.substringAfter("-")
        val edge = if (n1 < n2) Corridor(n1, n2) else Corridor(n2, n1)
        check(edge !in corridors) { "Duplicate edge" }
        corridors += edge
        caves += n1
        caves += n2
    }
    return CaveSystem(caves, corridors)
}

private typealias Cave = String

private val Cave.isSmall: Boolean get() = this.all { it.isLowerCase() }

private data class Corridor(val cave1: Cave, val cave2: Cave) {
    init {
        check(cave1 != cave2) { "Corridor cannot be a loop" }
        check(cave1 < cave2) { "Caves should be sorted" }
    }
}

private operator fun Corridor.contains(n: Cave): Boolean =
    n == cave1 || n == cave2

private fun Corridor.oppositeFrom(cave: Cave): Cave =
    if (cave == cave2) cave1 else cave2

private data class CaveSystem(
    val caves: Set<Cave>,
    val corridors: Set<Corridor>,
)

private fun CaveSystem.findPaths(
    paths: List<Path>,
    end: Cave,
    shouldEnterCave: (cave: Cave, path: Path) -> Boolean
): List<Path> =
    paths.flatMap { path -> findPaths(path, end, shouldEnterCave) }

private fun CaveSystem.findPaths(
    path: Path,
    end: Cave,
    shouldEnterCave: (cave: Cave, path: Path) -> Boolean
): List<Path> =
    when (val current = path.cave) {
        end -> listOf(path)
        else -> {
            corridors.filter { current in it }
                .map { it.oppositeFrom(current) }
                .filter { cave -> shouldEnterCave(cave, path) }
                .map { cave -> Path(cave, path) }
        }
    }

private data class Path(val cave: Cave, val from: Path? = null) : Iterable<Cave> {

    override fun toString(): String = when (from) {
        null -> cave
        else -> "$from,$cave"
    }

    override fun iterator(): Iterator<Cave> = object : Iterator<Cave> {
        private var next: Path? = this@Path

        override fun hasNext(): Boolean = next != null

        override fun next(): Cave =
            checkNotNull(next).also { next = it.from }.cave
    }
}

private operator fun Path.contains(cave: Cave): Boolean =
    when {
        this.cave == cave -> true
        this.from == null -> false
        else -> cave in this.from
    }

private fun Path.isAnySmallCaveVisitedTwice(): Boolean {
    val counts: Map<String, Int> = filter { it.isSmall }.groupingBy { it }.eachCount()
    return counts.values.any { it > 1 }
}

class PassagePathingTests {

    @Test
    fun `Parse reads caves and corridors`() {
        val graph = parseInput(listOf("a-b"))
        assertEquals(setOf("a", "b"), graph.caves)
        assertEquals(setOf(Corridor("a", "b")), graph.corridors)
    }

    @Test
    fun `Parse stores corridors with ordered caves`() {
        val graph = parseInput(listOf("b-a"))
        assertEquals(setOf("a", "b"), graph.caves)
        assertEquals(setOf(Corridor("a", "b")), graph.corridors)
    }

    @Test
    fun `Parse throws on duplicate corridor`() {
        assertThrows("Duplicate edge", IllegalStateException::class.java) {
            parseInput(listOf("a-b", "a-b"))
        }
    }

    @Test
    fun `Input meets assumptions`() {
        parseInput(readInput("input.txt"))
    }

    @Test
    fun `Path contains`() {
        val path = Path("c", Path("b", Path("a")))
        assertTrue("a" in path)
        assertTrue("b" in path)
        assertTrue("c" in path)
        assertFalse("d" in path)
    }

    @Test
    fun `Path iterator`() {
        val path = Path("c", Path("b", Path("a")))
        val iterator = path.iterator()
        assertTrue(iterator.hasNext())
        assertEquals("c", iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals("b", iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals("a", iterator.next())
        assertFalse(iterator.hasNext())
    }

    @Test
    fun `Path toString`() {
        val path = Path("c", Path("b", Path("a")))
        assertEquals("a,b,c", path.toString())
    }

    @Test
    fun `Any small cave visited twice`() {
        val path1 = Path("c", (Path("b", Path("a"))))
        assertFalse(path1.isAnySmallCaveVisitedTwice())
        val path2 = Path("b", (Path("a", Path("a"))))
        assertTrue(path2.isAnySmallCaveVisitedTwice())
        val path3 = Path("b", (Path("A", Path("A"))))
        assertFalse(path3.isAnySmallCaveVisitedTwice())
    }
}
