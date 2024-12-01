/**
 * DAY 11 - Ga;axies
 *
 * Part 1 immediately made me think of Bresenhan's algorithm for
 * plotting a line between two points on a grid in a step-by-step
 * process. But efficiently and algorithmic speed is not of the
 * essence here, so I implemented a naive way which simply summed
 * the vertical and horizontal differences.
 *
 * Part 1 was straightforward, but Part 2 failed immediately since
 * we ran out of memory expanding the maps by several 1,000,000
 * rows and columns!
 *
 * Solution was to keep track of the affected rows/columns and then
 * account for any of those in the shortest distance calcs.
 *
 */

// TODO: Need to track the expansions and store them, but not expand the map.
//  Then see how many expansions lay between two points and add those into the distance calcs

package aoc2023

import utils.println
import utils.readInput
import kotlin.math.abs

const val GAP = '.'
const val GALAXY = '#'

fun main() {

    data class Galaxy(
        val number: Int,
        val x: Int,
        val y: Int
    )

    val expandedRows = mutableListOf<Int>()
    val expandedCols = mutableListOf<Int>()
    val galaxies = mutableMapOf<Int, Galaxy>()

    /**
     * Create connected nodes from the sensor data
     */
    fun parseSkyMap(map: List<String>, expansion: Int = 2) {
//        map.forEach { println(it) }

        // Expand spaces in map
        map.forEachIndexed { y, row ->
            if (row.all { it == GAP }) {
                println("Expanding row $y")
                expandedRows.add(y)
            }
        }

        for (x in 0 until map[0].length) {
            var isGap = true
            map.forEach { row -> if (row[x] != GAP) isGap = false }
            if (isGap) {
                println("Expanding col $x")
                expandedCols.add(x)
            }
        }

        var index = 0
        var realY = 0
        map.forEachIndexed { y, row ->
            var realX = 0
            row.forEachIndexed { x, symbol ->
                if (symbol == GALAXY) galaxies[++index] = Galaxy(index, realX, realY)
                realX += if (x in expandedCols) expansion else 1
            }
            realY += if (y in expandedRows) expansion else 1
        }

        galaxies.forEach { println(it) }
    }

    fun sumOfShortestPaths(galaxies: Map<Int, Galaxy>): Long {
        var total = 0L
        for (from in galaxies.keys.min()..galaxies.keys.max()) {
            for (to in from + 1..galaxies.keys.max()) {
                val galFrom = galaxies[from]!!
                val galTo = galaxies[to]!!

//                val xStep = if (galFrom.x > galTo.x) -1 else 1
//                val yStep = if (galFrom.y > galTo.y) -1 else 1
//
//                var xExpansion = 0
//                for (x in galFrom.x..galTo.x step xStep) {
//                    if (x in expandedCols) xExpansion++
//                }
//                var yExpansion = 0
//                for (y in galFrom.y..galTo.y step yStep) {
//                    if (y in expandedRows) yExpansion++
//                }

                val xDiff = abs(galFrom.x - galTo.x)
                val yDiff = abs(galFrom.y - galTo.y)
                val steps = xDiff + yDiff
                total += steps
                
//                println("$from to $to is $steps : $total")
            }
        }
        return total
    }

    // Test data
    // For Part 1
    val testInput = readInput(2023, "Day11_test")
    parseSkyMap(testInput, 2)
    check(sumOfShortestPaths(galaxies) == 374L)

    // The real thing
    val input = readInput(2023, "Day11")
    // For Part 1
    parseSkyMap(input, 2)
    sumOfShortestPaths(galaxies).println()
    // For Part 2
//    parseSkyMap(input, 1000000)
//    sumOfShortestPaths(galaxies).println()
}
