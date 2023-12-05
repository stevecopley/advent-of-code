/**
 * DAY 05 - Seed Planting
 *
 *
 */

import utils.println
import utils.readInput

fun main() {
    fun lowestLocation(almanac: List<String>): Int {
        return almanac.size
    }

    // Check given test values:
    val testInput = readInput("Day05_test")
    check(lowestLocation(testInput) == 35)

    // Work through the real data
    val input = readInput("Day05")
    lowestLocation(input).println()
}
