/**
 * DAY 06 - Boat Race
 *
 * Pretty straightforward, and I'm enjoying working out
 * all the more Kotliny ways of doing things like parsing
 * the text, looping in different ways, etc.
 */

package aoc2023

import utils.println
import utils.readInput

fun main() {
    // For Part 1 (list of races)
    var raceTimes: List<Long>? = null
    var bestDists: List<Long>? = null
    // For Part 2 (single race)
    var raceTime = 0L
    var bestDist = 0L

    val whitespaces = "\\s+".toRegex()

    /**
     * Extract the list of race times and record distances from results
     */
    fun parseResults(results: List<String>) {
        raceTimes = results[0].drop(10).trim().split(whitespaces).map(String::toLong)
        bestDists = results[1].drop(10).trim().split(whitespaces).map(String::toLong)
        println("Times: $raceTimes")
        println("Distances: $bestDists")
    }

    /**
     * Construct the single race time and record distance from results
     */
    fun parseResultsCorrectly(results: List<String>) {
        raceTime = results[0].drop(10).trim().replace(" ", "").toLong()
        bestDist = results[1].drop(10).trim().replace(" ", "").toLong()
        println("Time: $raceTime")
        println("Distance: $bestDist")
    }

    /**
     * For a given race time and record distance, find the number
     * of ways that we can press the button to power up the boat
     * and still beat the record distance
     */
    fun raceWaysToWin(raceTime: Long, distToBeat: Long): Long {
        val minPress = 1
        val maxPress = raceTime - 1
        var waysToWin = 0L
        for (pressTime in minPress..maxPress) {
            val speed = pressTime
            val moveTime = raceTime - pressTime
            val distance = speed * moveTime
            if (distance > distToBeat) waysToWin++
        }
        return waysToWin
    }

    /**
     * Run through all the races and find the product of
     * all the times we can beat the current record
     */
    fun allWaysToWin(): Long {
        var totalWaysToWin = 1L
        raceTimes?.forEachIndexed { i, raceTime ->
            val distToBeat = bestDists?.get(i)!!
            val waysToWin = raceWaysToWin(raceTime, distToBeat)
            if (waysToWin > 0) totalWaysToWin *= waysToWin
        }
        return totalWaysToWin
    }

    // Test data
    val testInput = readInput(2023, "Day06_test")
    // For Part 1
    parseResults(testInput)
    check(allWaysToWin() == 288L)
    // For Part 2
    parseResultsCorrectly(testInput)
    check(raceWaysToWin(raceTime, bestDist) == 71503L)

    // The real thing
    val input = readInput(2023, "Day06")
    // For Part 1
    parseResults(input)
    allWaysToWin().println()
    // For Part 2
    parseResultsCorrectly(input)
    raceWaysToWin(raceTime, bestDist).println()
}
