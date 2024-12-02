/**
 * DAY 02 - Red-Nosed Reports
 *
 * Feels like there should be a more efficient way than my solution
 * But it's late and I'm tired after having my latest COVID shot!
 */

package aoc2024

import utils.println
import utils.readInput
import kotlin.math.abs
import kotlin.math.sign

fun main() {

    fun parseReports(input: List<String>): List<List<Int>> {
        val reports = mutableListOf<List<Int>>()
        for (line in input) {
            val values = line.split(' ')
            val levels = values.map { it -> it.toInt() }
            reports.add(levels)
        }
        return reports.toList()
    }

    fun safeReport(report: List<Int>): Boolean {
        var isSafe = true
        var directions = 0

        // Work thru pairs of levels
        for (i in 0 until report.size - 1) {
            // Find difference
            val diff = report[i + 1] - report[i]
            // Bail out if too large
            if (abs(diff) > 3) {
                isSafe = false
                break
            }
            // Otherwise sum the signs (to check if all in same direction)
            directions += diff.sign
        }

        // Didn't fail already, and all diffs in same direction?
        return isSafe && (abs(directions) == report.size - 1)
    }

    // Safe if all levels trending same way, and with differences <= 3
    fun safeReportCount(reports: List<List<Int>>): Int {
        var count = 0

        for (report in reports) {
            if (safeReport(report)) count++
        }

        return count
    }

    // Safe if all bar 0 or 1 levels trending same way, and with differences <= 3
    fun safeDampedReportCount(reports: List<List<Int>>): Int {
        var count = 0

        for (report in reports) {
            // Is this report safe already?
            if (safeReport(report)) {
                // Yep, so count and onto next
                count++
                continue
            }
            // Nope, so work thru removing a value and rechecking
            for (i in report.indices) {
                val dampedReport = report.toMutableList()
                dampedReport.removeAt(i)
                // Does this version work?
                if (safeReport(dampedReport)) {
                    // Yep, so count and no point checking more versions
                    count++
                    break
                }
            }
        }

        return count
    }

    // Check given test values:
    val testInput = readInput(2024, "Day02_test")
    val testReports = parseReports(testInput)
    check(safeReportCount(testReports) == 2)
    check(safeDampedReportCount(testReports) == 4)

    // Work through the real data
    val input = readInput(2024, "Day02")
    val reports = parseReports(input)
    safeReportCount(reports).println()
    safeDampedReportCount(reports).println()
}
