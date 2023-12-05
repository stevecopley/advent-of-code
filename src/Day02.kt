import utils.println
import utils.readInput

/**
 * DAY 02 - Cubes in a Bag
 *
 * This was pretty straightforward
 */

fun main() {
    val cubeLimits = mapOf("red" to 12, "green" to 13, "blue" to 14)

    /**
     * For a given game, find the max of each colour
     * Returns id and map of max counts
     */
    fun maxCounts(game: String): Pair<Int, Map<String, Int>> {
        // "Game XX: subset; subset; subset; subset"
        val gameInfo = game.split(": ")
        val id = gameInfo[0].substring(5).toInt()
        val subsets = gameInfo[1].split("; ")

        // Parse subsets to determine max counts for each colour
        val maxCount = mutableMapOf("red" to 0, "green" to 0, "blue" to 0)
        for (subset in subsets) {
            // "X colour, X colour, X colour"
            val cubes = subset.split(", ")
            for (cube in cubes) {
                val cubeInfo = cube.split(" ")
                val cubeCount = cubeInfo[0].toInt()
                val cubeColour = cubeInfo[1]
                if (cubeCount > maxCount[cubeColour]!!) {
                    maxCount[cubeColour] = cubeCount
                }
            }
        }

        return Pair(id, maxCount)
    }

    /**
     * Find total of game IDs that are valid
     */
    fun gameIDs(gameList: List<String>): Int {
        var total = 0
        for (game in gameList) {
            val (id, maxCount) = maxCounts(game)

            // Check if this subset is possible
            var possible = true
            for ((colour, count) in maxCount) {
                if (count > cubeLimits[colour]!!) {
                    possible = false
                }
            }
            // If we're good, can add in
            if (possible) total += id
        }
        return total
    }

    /**
     * Find total of game powers (max r * max g * max b)
     */
    fun gamePowers(gameList: List<String>): Int {
        var total = 0
        for (game in gameList) {
            val maxCount = maxCounts(game).second

            // Calc power of this game = max r * g * b
            var power = 1
            for (count in maxCount.values) {
                power *= count
            }

            total += power
        }
        return total
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(gameIDs(testInput) == 8)
    check(gamePowers(testInput) == 2286)

    val input = readInput("Day02")
    gameIDs(input).println()
    gamePowers(input).println()
}
