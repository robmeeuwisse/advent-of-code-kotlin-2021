package day17

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import readInput
import kotlin.math.max

/**
 * [Day 17: Trick Shot](https://adventofcode.com/2021/day/17)
 */
class TrickShot {

    @Test
    fun part1() {
        assertEquals(45, part1(Area(20..30, -10..-5)))
        assertEquals(7381, part1(readInput("input.txt").parseTargetArea()))
    }

    @Test
    fun part2() {
        assertEquals(112, part2(Area(20..30, -10..-5)))
        assertEquals(3019, part2(readInput("input.txt").parseTargetArea()))
    }
}

private fun part1(targetArea: Area): Int {
    var bestY = 0
    (0..1000).map { initialVelocityY ->
        val trajectory = Trajectory(0, initialVelocityY)

        var trajectoryPeakY = 0
        for (t in 0..Int.MAX_VALUE) {
            val y = trajectory.y(t)
            trajectoryPeakY = max(y, trajectoryPeakY)
            if (y <= targetArea.y.last) {
                if (y < targetArea.y.first) {
                    trajectoryPeakY = 0
                }
                break
            }
        }

        if (trajectoryPeakY > bestY) {
            bestY = trajectoryPeakY
        }
    }

    val result = bestY
    println("Part 1 answer: $result")
    return result
}

private fun part2(targetArea: Area): Int {
    val trajectories = mutableListOf<Trajectory>()
    for (initialVelocityX in 1 .. targetArea.x.last) {
        for (initialVelocityY in targetArea.y.first .. 1000) {
            val trajectory = Trajectory(initialVelocityX, initialVelocityY)

            for (t in 0 .. Int.MAX_VALUE) {
                val x = trajectory.x(t)
                val y = trajectory.y(t)
                if (x in targetArea.x && y in targetArea.y) {
                    trajectories.add(trajectory)
                    break
                }
                if (x > targetArea.x.last || y < targetArea.y.first) break
            }
        }
    }

    val result = trajectories.size
    println("Part 2 answer: $result")
    return result
}

data class Area(val x: IntRange, val y: IntRange)

private fun List<String>.parseTargetArea(): Area {
    val line = first()
    val ranges = line.substringAfter(": ").split(", ")
    val xRange = ranges[0].substringAfter("=").split("..").map(String::toInt)
    val yRange = ranges[1].substringAfter("=").split("..").map(String::toInt)
    return Area(
        x = IntRange(xRange[0], xRange[1]),
        y = IntRange(yRange[0], yRange[1]),
    )
}

private class Trajectory(
    val x0: Int,
    val y0: Int,
) {
    fun y(t: Int) = y0 * t - (t - 1) * t / 2
    fun x(t: Int) = when {
        t <= x0 -> x0 * t - (t - 1) * t / 2
        else -> x0 * x0 - (x0 - 1) * x0 / 2
    }

    override fun toString(): String = "initial velocity: $x0, $y0"
}

class TrickShotTest {
    @Test
    fun `Target area assumptions`() {
        val area = readInput("input.txt").parseTargetArea()
        assertTrue("X range should be positive", area.x.all { it >= 0 })
        assertTrue("Y range should be negative", area.y.all { it <= 0 })
        assertTrue("X range should not be reversed", area.x.first <= area.x.last)
        assertTrue("Y range should not be reversed", area.y.first <= area.y.last)
    }

    @Test
    fun `Trajectory y`() {
        (0..100).map { initialVelocityY -> Trajectory(0, initialVelocityY) }
            .forEach { trajectory ->
                val time = 0..100
                var currentVelocityY = trajectory.y0
                var currentY = 0
                val expected = time.map { currentY.also { currentY += currentVelocityY; currentVelocityY -= 1 } }
                val actual = time.map { trajectory.y(it) }
                assertEquals("Test case $trajectory", expected, actual)
            }
    }

    @Test
    fun `Trajectory x`() {
        (0..100).map { initialVelocityX -> Trajectory(initialVelocityX, 0) }
            .forEach { trajectory ->
                val time = 0..100
                var currentVelocityX = trajectory.x0
                var currentX = 0
                val expected = time.map {
                    currentX.also {
                        currentX += currentVelocityX
                        currentVelocityX = max(currentVelocityX - 1, 0)
                    }
                }
                val actual = time.map { trajectory.x(it) }
                assertEquals("Test case $trajectory", expected, actual)
            }
    }
}
