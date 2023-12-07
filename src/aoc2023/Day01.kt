/**
 * DAY 01 - Calibration
 *
 * This was fun. I think my solution worked out pretty
 * well, solving both parts with just a different set
 * of validation values... But maybe not... I don't know!
 */

package aoc2023

import utils.println
import utils.readInput

fun main() {
    val digits = "0 1 2 3 4 5 6 7 8 9".split(" ")
    val digitsWords = "zero one two three four five six seven eight nine".split(" ")
    val digitsFull = digits + digitsWords

    fun sumCalibrationValues(
        input: List<String>,
        validValues: List<String>
    ): Int {
        // Convert digit string (0-9 or zero-nine) to int
        fun String.asInt(): Int {
            val index = validValues.indexOf(this)
            return if (index > 9) index - 10 else index
        }

        var total = 0
        for (line in input) {
            val tens = line.findAnyOf(validValues)
            val units = line.findLastAnyOf(validValues)

            if (tens != null && units != null) {
                val tensDigit = tens.second.asInt()
                val unitsDigit = units.second.asInt()
                val value = tensDigit * 10 + unitsDigit
//                println("$line : $tensDigit, $unitsDigit -> $value")
                total += value
            }
        }

        return total
    }

    // Check given test values:
    val testInput1 = readInput(2023, "Day01_test1")
    check(sumCalibrationValues(testInput1, digits) == 142)
    val testInput2 = readInput(2023, "Day01_test2")
    check(sumCalibrationValues(testInput2, digitsFull) == 281)

    // Work through the real data
    val input = readInput(2023, "Day01")
    sumCalibrationValues(input, digits).println()
    sumCalibrationValues(input, digitsFull).println()
}
