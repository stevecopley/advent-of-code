/**
 * DAY 10 - Pipes
 *
 * Another really interesting one. Mapping the pipe was realtively
 * easy. I think I have a clean solution, but as always, I might have
 * over-engineered it!
 *
 * Part 2 prompted me to learn about edge crossings to determine
 * the enclosed area - I had heard of this before, but needed to
 * Google it to clarify.
 *
 * The 'junk' parts on the map, that are not part of the pipe,
 * initially caused me trouble until I just created a new, clean
 * map with only the pie on it. Might be a cleverer solution, but
 * this did work!
 */

package aoc2023

import utils.println
import utils.readInput

// Direction that we can travel in
enum class Dir { N, S, W, E }

// Symbols in the map
const val SPACE = '.'
const val NE = 'L'
const val NW = 'J'
const val SE = 'F'
const val SW = '7'
const val NS = '|'
const val WE = '-'
const val BEGIN = 'S'

fun main() {

    /**
     * Holds a pipe segment type / position and links forward / backwards
     */
    data class Pipe(
        val symbol: Char,
        val x: Int,
        val y: Int,
        var next: Pipe? = null,
        var prev: Pipe? = null
    ) {
        override fun toString(): String {
            return "${prev?.symbol} (${prev?.x}, ${prev?.y}) -> $symbol ($x, $y) -> ${next?.symbol} (${next?.x}, ${next?.y})"
        }
    }

    /**
     * Create connected nodes from the sensor data
     */
    fun parsePipeMap(mapData: List<String>): Pipe {
        val mapHeight = mapData.size
        val mapWidth = mapData[0].length
        var startX = -1
        var startY = -1

        // look for start
        mapData.forEachIndexed { y, dataLine ->
            val row = dataLine.toList()
            if (row.contains(BEGIN)) {
                startY = y
                startX = row.indexOf(BEGIN)
            }
        }

        println("Map: $mapWidth x $mapHeight")
        mapData.forEach { println(it) }
        println("Start at ($startX, $startY)")

        // Possible connecting pipes in each direction
        val linksN = arrayOf(NS, SW, SE)
        val linksS = arrayOf(NS, NW, NE)
        val linksW = arrayOf(WE, NE, SE)
        val linksE = arrayOf(WE, NW, SW)

        // Find a pipe connecting to start
        var dir = if (startY > 0 && mapData[startY - 1][startX] in linksN) Dir.N
        else if (startY < mapHeight - 1 && mapData[startY + 1][startX] in linksS) Dir.S
        else if (startX > 0 && mapData[startY][startX - 1] in linksW) Dir.W
        else if (startY > 0 && mapData[startY - 1][startX] in linksE) Dir.E
        else throw Exception("No connection to start")

        // Start our pipe
        val start = Pipe(BEGIN, startX, startY)

        // These maps are to simplify the update of the x and y
        val dX = mapOf(Dir.N to 0, Dir.S to 0, Dir.W to -1, Dir.E to 1)
        val dY = mapOf(Dir.N to -1, Dir.S to 1, Dir.W to 0, Dir.E to 0)

        // Move off in the pipe's direction, connecting pipes
        var currentPipe = start
        while (true) {
            // What is next?
            val nextX = currentPipe.x + dX[dir]!!
            val nextY = currentPipe.y + dY[dir]!!
            val next = mapData[nextY][nextX]

            // Create a new segment (or reconnect to start)
            val nextPipe = if (next == BEGIN) start else Pipe(next, nextX, nextY)
            // Link to next and back from next to us
            currentPipe.next = nextPipe
            nextPipe.prev = currentPipe

            // If we have completed the loop, get out
            if (nextPipe == start) break

            // Otherwise move onwards
            currentPipe = nextPipe
            // And see where we're heading
            dir = when {
                (dir == Dir.N && next == SE) ||
                        (dir == Dir.S && next == NE) ||
                        (dir == Dir.E && next == WE) -> Dir.E

                (dir == Dir.N && next == SW) ||
                        (dir == Dir.S && next == NW) ||
                        (dir == Dir.W && next == WE) -> Dir.W

                (dir == Dir.W && next == NE) ||
                        (dir == Dir.E && next == NW) ||
                        (dir == Dir.N && next == NS) -> Dir.N

                (dir == Dir.W && next == SE) ||
                        (dir == Dir.E && next == SW) ||
                        (dir == Dir.S && next == NS) -> Dir.S

                else -> throw Exception("Can't follow route in direction: $dir along pipe: $next")
            }
        }

        // Pass back the start of the pipe
        return start
    }

    /**
     * Given the start of a looping pipe, measure the circuit
     * length and then half to find the furthest distance away from start
     */
    fun farthestPoint(start: Pipe): Int {
        var currentPipe = start
        var routeLen = 0
        do {
            currentPipe.println()
            currentPipe = currentPipe.next!!
            routeLen++
        } while (currentPipe != start)

        return kotlin.math.ceil(routeLen / 2.0).toInt()
    }


    /**
     * Find enclosed area based on vertical edge crossings
     */
    fun enclosedArea(mapData: List<String>, start: Pipe): Int {
        val height = mapData.size
        val width = mapData[0].length

        // Create a clean map, with only the pipe route inside (no junk)
        val map = MutableList(height) { MutableList<Char>(width) { SPACE } }
        var currentPipe = start
        do {
            map[currentPipe.y][currentPipe.x] = currentPipe.symbol
            currentPipe = currentPipe.next!!
        } while (currentPipe != start)

        // Now parse the map, counting edge crossings
        var area = 0
        var lastCorner = ' '

        map.forEach { mapRow ->
            println(mapRow)
            var inside = false

            mapRow.forEach { symbol ->
                when (symbol) {
                    // Vertical edge is simple
                    NS -> inside = !inside

                    // start or left side corners trigger a switch
                    BEGIN, SE, NE -> {
                        inside = !inside
                        lastCorner = symbol
                    }

                    // but only flip back in certain cases
                    NW -> if (lastCorner == NE || lastCorner == BEGIN) inside = !inside
                    SW -> if (lastCorner == SE || lastCorner == BEGIN) inside = !inside

                    // count the space if we're inside
                    SPACE -> if (inside) area++
                }

                println("  $symbol, ${if (inside) "IN" else "OUT"}, $area")
            }
        }

        return area
    }

    // Test data
    // For Part 1
    val testInput1 = readInput(2023, "Day10_test1")
    val testPipeMap1 = parsePipeMap(testInput1)
    check(farthestPoint(testPipeMap1) == 4)
    val testInput2 = readInput(2023, "Day10_test2")
    val testPipeMap2 = parsePipeMap(testInput2)
    check(farthestPoint(testPipeMap2) == 8)
    // For Part 2
    val testInput3 = readInput(2023, "Day10_test3")
    val testPipeMap3 = parsePipeMap(testInput3)
    check(enclosedArea(testInput3, testPipeMap3) == 4)
    val testInput4 = readInput(2023, "Day10_test4")
    val testPipeMap4 = parsePipeMap(testInput4)
    check(enclosedArea(testInput4, testPipeMap4) == 8)
    val testInput5 = readInput(2023, "Day10_test5")
    val testPipeMap5 = parsePipeMap(testInput5)
    check(enclosedArea(testInput5, testPipeMap5) == 10)

    // The real thing
    val input = readInput(2023, "Day10")
    val pipeMap = parsePipeMap(input)
    // For Part 1
    farthestPoint(pipeMap).println()
    // For Part 2
    enclosedArea(input, pipeMap).println()
}
