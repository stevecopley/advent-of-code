/**
 * DAY 01 - Historian Hysteria
 *
 * Good warm-up!
 */

package aoc2024

import utils.println
import utils.readInput
import kotlin.math.abs

fun main() {

    fun parseIDs(input: List<String>): Pair<MutableList<Int>, MutableList<Int>> {
        val list1 = mutableListOf<Int>()
        val list2 = mutableListOf<Int>()
        // Parse data, creating two Int lists
        for (line in input) {
            val values = line.split("\\s+".toRegex())
            list1.add(values[0].toInt())
            list2.add(values[1].toInt())
        }
        return Pair(list1, list2)
    }

    fun sumDistances(list1: MutableList<Int>, list2: MutableList<Int>): Int {
        // Sort for easy comparison
        val sorted1 = list1.sorted()
        val sorted2 = list2.sorted()
        // Sum the differences between list pairs
        var total = 0
        for (i in sorted1.indices) {
            total += abs(sorted1[i] - sorted2[i])
        }
        return total
    }

    fun similarity(list1: MutableList<Int>, list2: MutableList<Int>): Int {
        var score = 0
        // Similarity is sum of value in list 1 x occurrences in list 2
        for (id in list1) {
            val occurances = list2.count { it == id }
            score += id * occurances
//            println("$id - $occurances - $score")
        }
        return score
    }

    // Check given test values:
    val testInput = readInput(2024, "Day01_test")
    val (list1test, list2test) = parseIDs(testInput)
    check(sumDistances(list1test, list2test) == 11)
    check(similarity(list1test, list2test) == 31)

    // Work through the real data
    val input = readInput(2024, "Day01")
    val (list1, list2) = parseIDs(input)
    sumDistances(list1, list2).println()
    similarity(list1, list2).println()
}
