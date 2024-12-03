/**
 * DAY 03 - Mull it over
 *
 * This was frustrating!
 *
 * Got part 1 working quickly using a Regex
 *
 * But part 2 eluded me for a while... I coded up two different solutions
 * to parse out the valid parts of each line.
 *
 * UNTIL... I realised that the valid/invalid parts continued over line
 * breaks - each line was not a separate block of memory!
 */

package aoc2024

import utils.println
import utils.readInput

fun main() {

    fun parseMuls(line: String): List<Pair<Int, Int>> {
        val regex = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")
        val matches = regex.findAll(line)
        val ints = matches.map {
            Pair(it.groupValues[1].toInt(), it.groupValues[2].toInt())
        }
        return ints.toList()
    }

    fun parseMemory(input: List<String>): List<Pair<Int, Int>> {
        val muls = mutableListOf<Pair<Int, Int>>()
        for (line in input) {
            muls.addAll(parseMuls(line))
        }
        return muls.toList()
    }

    /**
     * The Regex way...
     *
     * Find valid blocks:
     *  - following start of line or do()
     *  - ending in don't() or end of line
     */
    fun parseMemoryConditional(input: List<String>): List<Pair<Int, Int>> {
        // Treat memory as one contiguous block, not as separate lines
        val completeInput = input.joinToString()
        val regex = Regex("""^(.*?)don't\(\)|do\(\)(.*?)don't\(\)|do\(\)(.*?)$""")
        val matches = regex.findAll(completeInput)
        val enabledParts = matches.map {
            it.groupValues[1] + it.groupValues[2] + it.groupValues[3]
        }
        // Glue all matched blocks into one
        val enabled = enabledParts.joinToString()
        // Parse the final result
        return parseMuls(enabled).toList()
    }

    /**
     * The manual scanning way...
     *
     * Find valid blocks:
     *  - following start of line or do()
     *  - ending in don't() or end of line
     */
    fun parseMemoryConditional2(input: List<String>): List<Pair<Int, Int>> {
        val startMarker = "do()"
        val endMarker = "don't()"

        // This needs to be independent of the lines - not reset per line
        var enabled = true
        val muls = mutableListOf<Pair<Int, Int>>()

        for (line in input) {
            var parsedLine = ""
            for (i in line.indices) {
                if (enabled) {
                    if (i < line.length - endMarker.length &&
                        line.substring(i, i + endMarker.length) == endMarker
                    ) {
                        enabled = false
                    }
                } else {
                    if (i < line.length - startMarker.length &&
                        line.substring(i, i + startMarker.length) == startMarker
                    ) {
                        enabled = true
                    }
                }

                if (enabled) {
                    parsedLine += line[i]
                }
            }
            muls.addAll(parseMuls(parsedLine))
        }
        return muls.toList()
    }

    fun sumMuls(muls: List<Pair<Int, Int>>): Int {
        var sum = 0
        // Work thru pairs of values to be multiplies
        for (mul in muls) {
            val (num1, num2) = mul
            sum += num1 * num2
        }
        return sum
    }

    // Check given test values:
    //Part 1
    val testInput = readInput(2024, "Day03_test")
    val testMuls = parseMemory(testInput)
    check(sumMuls(testMuls) == 161)
    // Part 2
    val testInput2 = readInput(2024, "Day03_test2")
    val testConditionalMuls = parseMemoryConditional(testInput2)
    check(sumMuls(testConditionalMuls) == 48)
    // Alternative method
    val testConditionalMuls2 = parseMemoryConditional2(testInput2)
    check(sumMuls(testConditionalMuls2) == 48)

    // Work through the real data
    // Part 1
    val input = readInput(2024, "Day03")
    val muls = parseMemory(input)
    sumMuls(muls).println()
    // Part 2
    val conditionalMuls = parseMemoryConditional(input)
    sumMuls(conditionalMuls).println()
    // Alternative method
    val conditionalMuls2 = parseMemoryConditional2(input)
    sumMuls(conditionalMuls2).println()
}
