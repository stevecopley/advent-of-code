/**
 * DAY 05 - Seed Planting
 *
 * This was great. Got a nice data structure setup full
 * of mapping data from the almanac that could be easily
 * followed. Seemed to work really well and made Part 2 easy.
 *
 * Part 2, with its 100s of millions of seeds got my laptop
 * sweating! Had to implement a progress bar just to know it
 * was actually progressing!
 */

package aoc2023

import utils.println
import utils.readInput

fun main() {

    /**
     * Mapping corresponds to a line from the almanac
     * defining how source value maps to destination value
     */
    class Mapping(
        data: String
    ) {
        var destStart = 0L
        var destEnd = 0L
        var srcStart = 0L
        var srcEnd = 0L

        init {
            val (dest, src, length) = data.split(" ").map(String::toLong)
            destStart = dest
            destEnd = destStart + length - 1
            srcStart = src
            srcEnd = srcStart + length - 1
        }

        fun srcInRange(src: Long): Boolean {
            return src in srcStart..srcEnd
        }

        fun srcToDest(src: Long): Long {
            return if (srcInRange(src)) destStart + (src - srcStart) else src
        }

        override fun toString(): String {
            return "  [$srcStart-$srcEnd] -> [$destStart-$destEnd]"
        }
    }

    /**
     * MappingCollection corresponds to a block of the almanac
     * with a from->to mapping and a series of mapping ranges
     */
    class MappingCollection(
        collectionData: String
    ) {
        var from: String = "?"
        var to: String = "?"
        val mappingList = mutableListOf<Mapping>()

        init {
            val (dataFrom, dataTo) = collectionData.dropLast(5).split("-to-")
            from = dataFrom
            to = dataTo
        }

        fun srcToDest(src: Long): Long {
            for (mapping in mappingList) {
                if (mapping.srcInRange(src)) return mapping.srcToDest(src)
            }
            return src
        }

        override fun toString(): String {
            return "$from -> $to"
        }
    }

    val allMappings = mutableListOf<MappingCollection>()

    /**
     * Extract the seed data from the first line of almanac
     * Part 1: they are individual seeds
     */
    fun getSeeds(almanac: List<String>): MutableList<Long> {
        val seeds = mutableListOf<Long>()
        // Get the seeds from line 1
        seeds.addAll(almanac[0].substring(7).split(" ").map(String::toLong))
        println(seeds)
        return seeds
    }

    /**
     * Extract the seed data from the first line of almanac
     * Part 2: this time they are start / length pairs
     */
    fun getSeedRanges(almanac: List<String>): MutableList<Pair<Long, Long>> {
        val seedRanges = mutableListOf<Pair<Long, Long>>()
        // Get the seed data from line 1
        val seedData = almanac[0].substring(7).split(" ").map(String::toLong)
        // Generate full seed list based on start / length pairs
        for (i in seedData.indices step 2) {
            val start = seedData[i]
            val end = start + seedData[i + 1] - 1
            seedRanges.add(Pair(start, end))
        }
        println(seedRanges)
        return seedRanges
    }

    /**
     * Extract the data from the almanac
     * Skip over seeds on first line
     * Blocks corresponding to mapping sets
     */
    fun parseAlmanac(almanac: List<String>) {
        allMappings.clear()
        var currentMappings: MappingCollection? = null

        for (i in 1 until almanac.size) {
            val line = almanac[i]

            when {
                // Ignore empty lines
                line.isEmpty() -> continue

                // This is the start of a block
                line.contains("map") -> {
                    // Create a new collection ready to use
                    currentMappings = MappingCollection(line)
                    allMappings.add(currentMappings)
                }

                // Must be a data line, so add to the currect collection
                else -> currentMappings?.mappingList?.add(Mapping(line))
            }
        }

        for (mappings in allMappings) {
            println(mappings)
            for (mapping in mappings.mappingList) {
                println(mapping)
            }
        }
    }

    /**
     * For a given seed, follow all of the mappings
     * extracted from the almanac to find a location
     * and return it
     */
    fun followMappings(seed: Long): Long {
        var current = seed
        // Pass the seed data through all the mappings
        for (mappings in allMappings) {
//            print("  ${mappings.from}: $current -> ${mappings.to}: ")
            current = mappings.srcToDest(current)
//            println(current)
        }
        return current
    }

    /**
     * Work through all seeds and follow the mappings
     * Track the lowest final location and return
     */
    fun lowestLocation(seeds: List<Long>): Long {
        var lowest = Long.MAX_VALUE
        // Work through all seeds
        for (seed in seeds) {
            println("Seed: $seed")

            val location = followMappings(seed)
            // Have a new lowest?
            if (location < lowest) lowest = location
        }
        return lowest
    }

    /**
     * Work through all seed ranges and follow the mappings
     * Track the lowest final location and return
     */
    fun lowestLocation(seedRanges: List<Pair<Long, Long>>): Long {
        var lowest = Long.MAX_VALUE
        // Work through all seed ranges
        for (range in seedRanges) {
            val start = range.first
            val end = range.second
            val step = (start - end) / 10

            println("Seeds: $start-$end")
            // And thru all the seeds in that range
            for (seed in start..end) {
                // Just show progress every 10% or so
                if (seed % step == 0L) {
                    val progress = ((seed - start) * 50 / (end - start)).toInt()
                    println("  $seed ${"]".repeat(progress)}")
                }

                val location = followMappings(seed)
                // Have a new lowest?
                if (location < lowest) lowest = location
            }
        }
        return lowest
    }

    // Test data
    val testInput = readInput(2023, "Day05_test")
    parseAlmanac(testInput)
    // Based on individual seed values for Part 1
    val testSeeds = getSeeds(testInput)
    check(lowestLocation(testSeeds) == 35L)
    // Based on seed ranges for Part 2
    val testSeedRanges = getSeedRanges(testInput)
    check(lowestLocation(testSeedRanges) == 46L)

    // The real thing
    val input = readInput(2023, "Day05")
    parseAlmanac(input)
    // Based on individual seed values for Part 1
    val seeds = getSeeds(input)
    lowestLocation(seeds).println()
    // Based on seed ranges for Part 2
    val seedRanges = getSeedRanges(input)
    lowestLocation(seedRanges).println()
}
