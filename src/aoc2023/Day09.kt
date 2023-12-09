/**
 * DAY 08 - Oasis Sensor
 *
 * Enjoyed this one. Took me a while to work out the recursion
 * but worked in the end. Duplicated the forwards / backwards
 * code rather than trying to do anything clever :-D
 */

package aoc2023

import utils.println
import utils.readInput


fun main() {
    /**
     * Create connected nodes from the sensor data
     */
    fun parseSensorData(sensorData: List<String>): List<MutableList<Int>> {
        val histories = mutableListOf<MutableList<Int>>()
        sensorData.forEach { dataRow ->
            val history = dataRow.split(" ").map(String::toInt).toMutableList()
            histories.add(history)
        }
        println("Parsed...")
        histories.forEach { println(it) }
        return histories
    }

    /**
     * Given a list, pass back a new list of the differences between values
     */
    fun findDiffs(list: List<Int>): List<Int> {
        val diffs = mutableListOf<Int>()
        for (i in 0 until list.size - 1) {
            diffs.add(list[i + 1] - list[i])
        }
        return diffs
    }

    /**
     * Recursively dig down into difference lists, using return values
     * on the way back up to predict the next value in the given list
     */
    fun extrapolateForward(readings: List<Int>): Int {
        println("Extrapolating Forwards $readings...")
        if (readings.all { it == 0 }) return 0
        val diffs = findDiffs(readings)
        val nextValue = readings.last() + extrapolateForward(diffs)
        println("$readings -> $nextValue")
        return nextValue
    }

    /**
     * As above, but for list front
     */
    fun extrapolateBackward(readings: List<Int>): Int {
        println("Extrapolating Backwards $readings...")
        if (readings.all { it == 0 }) return 0
        val diffs = findDiffs(readings)
        val prevValue = readings.first() - extrapolateBackward(diffs)
        println("$prevValue <- $readings")
        return prevValue
    }

    /**
     * Extend each list of a given collection of sensor data values with predicted values
     */
    fun makeForwardPredictions(histories: List<List<Int>>): List<List<Int>> {
        val newHistories = mutableListOf<MutableList<Int>>()
        histories.forEach { history ->
            val newHistory = history.toMutableList()
            newHistory.add(extrapolateForward(history))
            println("\nPrediction: $newHistory\n")
            newHistories.add(newHistory)
        }
        return newHistories
    }

    /**
     * As above, but extending front of lists
     */
    fun makeBackwardPredictions(histories: List<List<Int>>): List<List<Int>> {
        val newHistories = mutableListOf<MutableList<Int>>()
        histories.forEach { history ->
            val newHistory = history.toMutableList()
            newHistory.add(0, extrapolateBackward(history))
            println("\nPrediction: $newHistory\n")
            newHistories.add(newHistory)
        }
        return newHistories
    }

    /**
     * Add up predictions from end of sensor data lists
     */
    fun sumOfForwardPredictions(histories: List<List<Int>>): Int {
        return histories.sumOf { history -> history.last() }
    }

    /**
     * Add up predictions from start of sensor data lists
     */
    fun sumOfBackwardPredictions(histories: List<List<Int>>): Int {
        return histories.sumOf { history -> history.first() }
    }

    
    // Test data
    val testInput = readInput(2023, "Day09_test")
    val testHistories = parseSensorData(testInput)
    // For Part 1
    val newTestHistories1 = makeForwardPredictions(testHistories)
    check(sumOfForwardPredictions(newTestHistories1) == 114)
    // For Part 2
    val newTestHistories2 = makeBackwardPredictions(testHistories)
    check(sumOfBackwardPredictions(newTestHistories2) == 2)

    // The real thing
    val input = readInput(2023, "Day09")
    val histories = parseSensorData(input)
    // For Part 1
    val newHistories1 = makeForwardPredictions(histories)
    sumOfForwardPredictions(newHistories1).println()
    // For Part 2
    val newHistories2 = makeBackwardPredictions(histories)
    sumOfBackwardPredictions(newHistories2).println()
}
