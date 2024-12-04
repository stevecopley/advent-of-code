/**
 * DAY 04 - Ceres Search
 *
 * Enjoyed this one. May have over-engineered the solution (I've made
 * it able to work with any words of any length)
 */

package aoc2024

import utils.println
import utils.readInput

fun main() {

    // PART 1 ---------------------------------------------------

    fun checkInDirection(
        puzzle: List<String>,
        x: Int, y: Int,
        direction: Pair<Int, Int>,
        word: String
    ): Boolean {
        val width = puzzle[0].length
        val height = puzzle.size
        val steps = word.length
        val (dx, dy) = direction

        for (i in 0 until steps) {
            val xCheck = x + (dx * i)
            val yCheck = y + (dy * i)
            if (xCheck < 0 || xCheck >= width || yCheck < 0 || yCheck >= height) return false

            val puzzleChar = puzzle[yCheck][xCheck]
            val wordChar = word[i]
            if (puzzleChar != wordChar) return false
        }
        return true
    }

    fun wordCount(input: List<String>, word: String): Int {
        val width = input[0].length
        val height = input.size
        val directions = listOf(
            Pair(0, -1),    // N
            Pair(1, -1),    // NE
            Pair(1, 0),     // E
            Pair(1, 1),     // SE
            Pair(0, 1),     // S
            Pair(-1, 1),    // SW
            Pair(-1, 0),    // W
            Pair(-1, -1)    // NW
        )

        var count = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                for (direction in directions) {
                    if (checkInDirection(input, x, y, direction, word)) count++
                }
            }
        }
        return count
    }

    // PART 2 ---------------------------------------------------

    fun checkCrossAtLocation(
        puzzle: List<String>,
        x: Int, y: Int,
        direction: Pair<Int, Int>,
        word: String
    ): Boolean {
        val width = puzzle[0].length
        val height = puzzle.size
        val steps = word.length
        val (dx, dy) = direction
        val xStart = x - (dx * steps / 2)
        val yStart = y - (dy * steps / 2)

        for (i in 0 until steps) {
            val xCheck = xStart + (dx * i)
            val yCheck = yStart + (dy * i)
            if (xCheck < 0 || xCheck >= width || yCheck < 0 || yCheck >= height) return false

            val puzzleChar = puzzle[yCheck][xCheck]
            val wordChar = word[i]
            if (puzzleChar != wordChar) return false
        }
        return true
    }

    fun wordCrossCount(input: List<String>, word: String): Int {
        val width = input[0].length
        val height = input.size
        val directions = listOf(
            Pair(1, -1),    // NE
            Pair(1, 1),     // SE
            Pair(-1, 1),    // SW
            Pair(-1, -1)    // NW
        )

        var count = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                var locationCount = 0
                for (direction in directions) {
                    if (checkCrossAtLocation(input, x, y, direction, word)) locationCount++
                }
                if (locationCount == 2) count++
            }
        }
        return count
    }

    // Check given test values:
    //Part 1
    val testInput = readInput(2024, "Day04_test")
    check(wordCount(testInput, "XMAS") == 18)
    check(wordCrossCount(testInput, "MAS") == 9)

    // Work through the real data
    // Part 1
    val input = readInput(2024, "Day04")
    wordCount(input, "XMAS").println()
    wordCrossCount(input, "MAS").println()
}
