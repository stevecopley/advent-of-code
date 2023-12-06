import utils.println
import utils.readInput

/**
 * DAY 03 - Parts Schematic
 *
 * Fun challenge and I had a decent setup after
 * part 1 that helped for part 2. But...
 *
 * Not sure I did this in a very efficient way.
 * Lots of nested loops needed to scan the positions
 * above / below and left / right.
 */

fun main() {
    data class Part(val x: Int, val y: Int, val number: Int)
    data class Gear(val x: Int, val y: Int) {
        var ratio = 0

        override fun toString(): String {
            return "Gear(x=$x, y=$y, ratio=$ratio)"
        }
    }

    val digits = "0123456789".toList()

    val parts = mutableListOf<Part>()
    val gears = mutableListOf<Gear>()
    val symbols = mutableListOf<Char>()

    /**
     * Scan for numbers, symbols and gears (*)
     */
    fun scanSchematic(schematic: List<String>) {
        val schematicWidth = schematic[0].length
        val schematicHeight = schematic.size

        // Gather all parts and gears, and also symbols used
        for (y in 0 until schematicHeight) {
            var numVal = 0
            var numX = 0

            for (x in 0 until schematicWidth) {
                val char = schematic[y].get(x)

                // Found a number?
                if (char in digits) {
                    if (numVal == 0) numX = x
                    if (numVal > 0) numVal *= 10
                    numVal += digits.indexOf(char)
                } else {
                    // Save any numbers we were processing
                    if (numVal > 0) parts.add(Part(numX, y, numVal))
                    numVal = 0

                    when {
                        // Found a gear?
                        char == '*' -> {
                            gears.add(Gear(x, y))
                            if (char !in symbols) symbols.add(char)
                        }

                        // Found another type of symbol?
                        char != '.' -> {
                            if (char !in symbols) symbols.add(char)
                        }
                    }
                }
            }

            // Save any final numbers we were processing
            if (numVal > 0) parts.add(Part(numX, y, numVal))
        }
    }

    /**
     * Remove invalid parts (not touching symbol)
     */
    fun validateParts(schematic: List<String>) {
        val schematicWidth = schematic[0].length
        val schematicHeight = schematic.size

        // Use iterator to allow removal from list within loop
        val partsIterator = parts.iterator()
        while (partsIterator.hasNext()) {
            val part = partsIterator.next()

            val numLen = part.number.length()
            val left = if (part.x > 0) part.x - 1 else 0
            val right = if (part.x + numLen < schematicWidth) part.x + numLen else schematicWidth - 1
            val top = if (part.y > 0) part.y - 1 else 0
            val bottom = if (part.y + 1 < schematicHeight) part.y + 1 else schematicHeight - 1

            var symbolAdjacent = false
            // Scan across top and bottom
            for (x in left..right) {
                if (
                    (top < part.y && schematic.get(top).get(x) in symbols) ||
                    (bottom > part.y && schematic.get(bottom).get(x) in symbols)
                ) {
                    symbolAdjacent = true
                    break
                }
            }
            // Check left and right
            if (
                (left < part.x && schematic.get(part.y).get(left) in symbols) ||
                (right >= part.x + numLen && schematic.get(part.y).get(right) in symbols)
            ) {
                symbolAdjacent = true
            }

//            println("${part.number} [$numLen] (${part.x}, ${part.y}) [$top $right $bottom $left] $symbolAdjacent")

            if (!symbolAdjacent) partsIterator.remove()
        }
    }

    /**
     * Remove invalid gears (not touching exactly two parts)
     * and for valid ones, calculate gear ratio (product of part nos.)
     */
    fun validateGears(schematic: List<String>) {
        val schematicWidth = schematic[0].length
        val schematicHeight = schematic.size

        // Use iterator to allow removal from list within loop
        val gearsIterator = gears.iterator()
        while (gearsIterator.hasNext()) {
            val gear = gearsIterator.next()

            val left = if (gear.x > 0) gear.x - 1 else 0
            val right = if (gear.x + 1 < schematicWidth) gear.x + 1 else schematicWidth - 1
            val top = if (gear.y > 0) gear.y - 1 else 0
            val bottom = if (gear.y + 1 < schematicHeight) gear.y + 1 else schematicHeight - 1

            var partCount = 0
            var gearRatio = 1

            for (part in parts) {
                val numLen = part.number.length()
                var partInContact = false

                // Check for part above
                if (top < gear.y &&
                    part.y == top &&
                    part.x <= right &&
                    part.x + numLen > left
                ) partInContact = true

                // Check for part below
                else if (bottom > gear.y &&
                    part.y == bottom &&
                    part.x <= right &&
                    part.x + numLen > left
                ) partInContact = true

                // Check for part left
                else if (part.y == gear.y &&
                    part.x + numLen == gear.x
                ) partInContact = true

                // Check for part right
                else if (part.y == gear.y &&
                    part.x == gear.x + 1
                ) partInContact = true

                if (partInContact) {
                    partCount += 1
                    gearRatio *= part.number
                }
            }

//            println("(${gear.x}, ${gear.y}) [$top $bottom] $partCount $gearRatio")

            if (partCount == 2) {
                gear.ratio = gearRatio
            } else {
                gearsIterator.remove()
            }
        }
    }

    /**
     * Process the schematic, resulting in list of valid parts,
     * gears and also the symbols used throughout
     */
    fun parseSchematic(schematic: List<String>) {
        parts.clear()
        gears.clear()
        symbols.clear()

        scanSchematic(schematic)
        validateParts(schematic)
        validateGears(schematic)

//        println(symbols)
//        for (part in parts) println(part)
//        for (gear in gears) println(gear)
    }

    /**
     * Sum the numbers of all parts (touching symbols)
     */
    fun sumPartNumbers(): Int {
        var total = 0
        for (part in parts) {
            total += part.number
        }
        return total
    }

    /**
     * Sum the gear ratios of all gears (touching two parts)
     */
    fun sumGearRatios(): Int {
        var total = 0
        for (gear in gears) {
            total += gear.ratio
        }
        return total
    }


    // Test data checks
    val testInput = readInput("Day03_test")
    parseSchematic(testInput)
    check(sumPartNumbers() == 4361)
    check(sumGearRatios() == 467835)

    // real data
    val input = readInput("Day03")
    parseSchematic(input)
    sumPartNumbers().println()
    sumGearRatios().println()
}

/**
 * Extension function for digit count of an Int
 */
fun Int.length(): Int {
    return when (this) {
        in -9..9 -> 1
        else -> 1 + (this / 10).length()
    }
}

