@file:Suppress("TestFunctionName")

package day15

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Test
import readInput
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * [Day 15: Chiton](https://adventofcode.com/2021/day/15)
 */
class Chiton {

    @Test
    fun part1() {
        assertEquals(40, part1(readInput("input_test.txt")))
        assertEquals(685, part1(readInput("input.txt")))
    }

    @Test
    fun part2() {
        assertEquals(315, part2(readInput("input_test.txt")))
        assertEquals(2995, part2(readInput("input.txt")))
    }
}

private fun part1(input: List<String>): Int {

    val astar = AstarSearch(input.parseRisks())

    while (!astar.isFinished) {
        astar.iterate()
    }

    val result = astar.closedPaths.entries.last().value.cost
    println("Part 1 answer: $result")
    return result
}

private fun part2(input: List<String>): Int {
    val astar = AstarSearch(input.parseRisksTiled())

    while (!astar.isFinished) {
        astar.iterate()
    }

    val result = astar.closedPaths.entries.last().value.cost
    println("Part 2 answer: $result")
    return result
}

private fun List<String>.parseRisks(): Grid {
    val width = this.first().length
    check(all { it.length == width })
    val height = this.size
    val values = flatMap { it.map(Char::digitToInt) }
    return Grid(width, height, values.toMutableList())
}

private fun List<String>.parseRisksTiled(tileCount: Int = 5): Grid {
    val inputWidth = this.first().length
    check(all { it.length == inputWidth })
    val inputHeight = this.size
    val inputValues = flatMap { it.map(Char::digitToInt) }
    val width = tileCount * inputWidth
    val height = tileCount * inputHeight
    val values = MutableList(width * height) { index ->
        val x = index % width
        val y = index / width
        val tileX = x / inputWidth
        val inputX = x % inputWidth
        val tileY = y / inputHeight
        val inputY = y % inputHeight
        val value = inputValues[inputY * inputHeight + inputX]
        1 + (value - 1 + tileX + tileY) % 9
    }
    return Grid(width, height, values)
}

private class Grid(
    val width: Int,
    val height: Int,
    private val values: MutableList<Int> = MutableList(width * height) { 0 },
) {
    val indicesX = 0 until width
    val indicesY = 0 until height
    val area = width * height

    init {
        check(values.size == area)
    }

    operator fun get(x: Int, y: Int): Int = values[width * y + x]
    operator fun get(p: Position): Int = get(p.x, p.y)

    override fun toString(): String =
        values.chunked(width).joinToString("\n") { it.joinToString("") }

    data class Position(val x: Int, val y: Int) {
        override fun toString(): String = "($x, $y)"
    }

    operator fun contains(p: Position): Boolean =
        p.x in indicesX && p.y in indicesY
}

private data class Path(
    val p: Grid.Position,
    val parent: Path?,
    val cost: Int = 0,
) {
    override fun toString(): String = if (parent == null) "$p $cost" else "$p $cost > $parent"
}

private class AstarSearch(
    val risks: Grid,
) {
    private val startPosition: Grid.Position = Grid.Position(0, 0)
    private val endPosition: Grid.Position = Grid.Position(risks.indicesX.last, risks.indicesY.last)
    private val totalCostComparator = Comparator<Path> { path1, path2 -> totalCost(path1) - totalCost(path2) }

    val fringePaths = mutableListOf(Path(startPosition, null))
    val closedPaths = mutableMapOf<Grid.Position, Path>()
    var isFinished: Boolean = false

    fun iterate() {
        fringePaths.sortWith(totalCostComparator)
        val current = fringePaths.firstOrNull()

        if (current != null) {
            fringePaths.remove(current)
            closedPaths[current.p] = current
        }

        if (current == null || current.p == endPosition) {
            isFinished = true
            return
        }

        sequenceOf(
            Grid.Position(current.p.x + 1, current.p.y),
            Grid.Position(current.p.x, current.p.y + 1),
            Grid.Position(current.p.x - 1, current.p.y),
            Grid.Position(current.p.x, current.p.y - 1),
        )
            .filter { position ->
                position in risks && position !in closedPaths.keys
            }
            .map { position ->
                val cost = current.cost + risks[position]
                Path(position, current, cost)
            }
            .forEach { neighbour ->
                val fringePath = fringePaths.find { it.p == neighbour.p }
                if (fringePath == null) {
                    fringePaths.add(neighbour)
                } else if (neighbour.cost < fringePath.cost) {
                    fringePaths.remove(fringePath)
                    fringePaths.add(neighbour)
                }
            }
    }

    private fun totalCost(path: Path): Int = path.cost + heuristic(path.p)
    private fun heuristic(p: Grid.Position): Int = abs(p.x - endPosition.x) + abs(p.y - endPosition.y)
}

class ChitonTests {

    @Test
    fun `Parse grid`() {
        val grid = readInput("input_test.txt").parseRisks()
        println(grid)
    }

    @Test
    fun `Parse grid x 5`() {
        val grid = readInput("input_test.txt").parseRisksTiled()
        println(grid)
    }
}

object Visualize {

    @JvmStatic
    fun main(vararg args: String) = application {
        Window(
            onCloseRequest = ::exitApplication,
            title = Chiton::javaClass.name,
            state = rememberWindowState(width = 1000.dp, height = 1000.dp),
        ) {
            MaterialTheme {
                Visualize()
            }
        }
    }
}

@Composable
private fun Visualize() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        var progress by remember { mutableStateOf(0f) }
        var grids by remember { mutableStateOf<List<GridViewState>?>(null) }

        when (val states = grids) {
            null -> CircularProgressIndicator(progress)
            else -> Visualize(states)
        }

        LaunchedEffect(Unit) {
            launch(Dispatchers.Default) {
                grids = buildList {
                    val astar = AstarSearch(readInput("input.txt").parseRisks())
                    var steps = 0
                    while (isActive) {
                        progress = (steps++).toFloat() / astar.risks.area
                        add(gridViewStateOf(astar))
                        if (astar.isFinished) break
                        astar.iterate()
                    }
                }
            }
        }
    }
}

private fun gridViewStateOf(astar: AstarSearch): GridViewState {
    return GridViewState(
        width = astar.risks.width,
        height = astar.risks.height,
        rows = astar.risks.indicesY.map { y ->
            astar.risks.indicesX.map { x ->
                cellViewStateOf(astar, x, y)
            }
        }
    )
}

private fun cellViewStateOf(astar: AstarSearch, x: Int, y: Int): CellViewState {
    val fringePath = astar.fringePaths.find { it.p.y == y && it.p.x == x }
    val closedPath = astar.closedPaths[Grid.Position(x, y)]
    val risk = astar.risks[x, y]
    val background = when {
        fringePath != null ->
            Color.Yellow
        closedPath != null ->
            closedColors[closedPath.cost % closedColors.size]
        else ->
            unvisitedColors[risk]
    }
    return CellViewState(
        risk = risk.toString(),
        background = background,
    )
}

private val closedColors = Array(100) { lerp(Color.Blue, Color.Green, it.toFloat() / 100) }
private val unvisitedColors = Array(10) { lerp(Color.White, Color.Gray, it.toFloat() / 10) }

private data class GridViewState(
    val width: Int,
    val height: Int,
    val rows: List<List<CellViewState>>,
)

private data class CellViewState(
    val risk: String,
    val background: Color,
)

@Composable
private fun Visualize(
    states: List<GridViewState>,
) {
    var sliderValue by remember { mutableStateOf(0f) }
    val stateIndex by derivedStateOf { sliderValue.roundToInt() }
    Column {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Slider(
                modifier = Modifier.weight(1f),
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = 0f..states.lastIndex.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.DarkGray,
                    activeTrackColor = Color.LightGray,
                    inactiveTrackColor = Color.LightGray,
                )
            )
            val maxSteps = states.lastIndex.toString()
            val currentStep = stateIndex.toString().padStart(maxSteps.length, '0')
            Text("$currentStep/$maxSteps")
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
                .background(Color.DarkGray)
                .padding(16.dp),
        ) {
            Visualize(
                modifier = Modifier.align(Alignment.Center),
                state = states[stateIndex]
            )
        }
    }
}

@Composable
private fun Visualize(
    state: GridViewState,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        state.rows.forEach { row ->
            Row {
                row.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(cell.background)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = cell.risk,
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.body2,
                        )
                    }
                }
            }
        }
    }
}
