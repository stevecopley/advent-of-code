/**
 * DAY 08 - Desert Map
 *
 * Part 1 was good to code up. Tried to come up with a neat solution
 * that worked, and I think I did.
 *
 * But... Part 2 was problematic. I coded up a solution pretty quickly,
 * building on the Part 1 structures I had in place, and... It never
 * found a solution! So, either my code is buggy, or it's so inefficient
 * that it doesn't ever get to the solution (I left it running for a couple
 * of hours!) I really can't think / see how to speed this up.
 *
 * Ok, so peeking at the JetBrains video for this day...
 * https://www.youtube.com/live/RSq35eSpsII?si=zbY-lT1zk8kojw8D&t=1873
 *
 * I can see that the solution is a very large value, and my naive algorithm
 * would not have got to it for days! Some sort of shortcut / clever
 * algorithm is required...
 *
 * Ok, watched a little more of that video, and it seems that the routes through
 * the map cycle, but are out of sync initially. Key is to find where they
 * fall into sync. This can be brute forced once the length of the cycles are
 * discovered.... Let's have a go.
 *
 * Yes... A bit of refactoring to allow use of the Part 1 route length calc
 *        Then had to work out that I needed the Least Common Multiple
 *        (i.e. the first point at which all route lengths coincide)
 */

package aoc2023

import utils.println
import utils.readInput

const val LEFT = 'L'
const val RIGHT = 'R'
const val START = "AAA"
const val END = "ZZZ"


fun main() {
    /**
     * Holds a map node, with left/right connections
     */
    data class Node(
        val name: String,
        var left: Node? = null,
        var right: Node? = null
    )

    /**
     * The full map, including all map nodes and the direction instructions
     */
    data class FullMap(
        val route: List<Char>,
        val nodes: Map<String, Node>
    )

    /**
     * Create connected nodes from the map data
     */
    fun parseMap(mapData: List<String>): FullMap {
        val route = mapData[0].toList()
        val nodes = mutableMapOf<String, Node>()

        // Grab the map node data
        for (i in 2 until mapData.size) {
            val (name, connections) = mapData[i].split(" = ")
            val (left, right) = connections.drop(1).dropLast(1).split(", ")

            // Create a new node if needed
            if (nodes[name] == null) nodes[name] = Node(name)
            // Add in the left / right nodes if they don't already exist from previous parsing
            if (nodes[left] == null) nodes[left] = Node(left)
            if (nodes[right] == null) nodes[right] = Node(right)

            // Connect our new node to the left/right nodes
            nodes[name]?.left = nodes[left]
            nodes[name]?.right = nodes[right]
        }

        println("Route: $route")
        nodes.forEach { (name, node) -> println("${node.left?.name} <- $name -> ${node.right?.name}") }

        return FullMap(route, nodes)
    }


    /**
     * for a given node and direction, get the left/right node
     */
    fun nextNode(node: Node, direction: Char): Node {
        return when (direction) {
            LEFT -> node.left!!
            RIGHT -> node.right!!
            else -> throw Exception("Bad direction: $direction")
        }
    }

    /**
     * Follow the map until we get to the end point, counting steps
     */
    fun totalSteps(route: List<Char>, start: Node): Int {
        var routeIndex = 0
        var node = start
        var steps = 0
        val endChar = END.last()

        println("START ${node.name}")

        // Generalised the end-check here to look for a final Z
        // since this is true for Part 1 ending condition of ZZZ
        // and also works for Part 2 condition of __Z
        while (node.name.last() != endChar) {
            val direction = route[routeIndex]
            node = nextNode(node, direction)
            // Move through the directions, wrapping if needed
            routeIndex = (routeIndex + 1) % route.size
            steps++

            println("$direction -$steps-> ${node.name}")
        }

        println("END")
        return steps
    }


    /**
     * NOTE: This is a naive, brute-force solution and just
     *       won't give a solution in any sort of reasonable
     *       timeframe. Leaving it here for posterity!
     *
     * As above, but running multiple routes in parallel
     * Begin from any node that ends in 'A'
     * We're done when all routes are at nodes ending in 'Z'
     */
    fun totalParallelSteps(map: FullMap): Long {
        var routeIndex = 0
        var nodes = map.nodes.filter { (name, _) -> name.last() == START.last() }.values.toList()
        var steps = 0L
        val endChar = END.last()

        nodes.forEach { println("START ${it.name}") }

        while (nodes.any { it.name.last() != endChar }) {
            val direction = map.route[routeIndex]
            nodes = nodes.map { node -> nextNode(node, direction) }
            routeIndex = (routeIndex + 1) % map.route.size
            steps++

//            if (steps % 10000000L == 0L) {
            if (nodes.count { it.name.last() == endChar } > 2) {
                println("$direction -$steps-")
                nodes.forEach { println("         -> ${it.name}") }
            }
        }

        println("END")
        return steps
    }

    /**
     * Find out how long the looping routes for the six start points
     * are so that we can then find out where they coincide
     */
    fun findRouteLengths(map: FullMap): Map<String, Int> {
        val routeLengths = mutableMapOf<String, Int>()
        // Get all start nodes
        val startNodes = map.nodes.filter { (name, _) -> name.last() == START.last() }.values.toList()

        startNodes.forEach { println("START ${it.name}") }

        startNodes.forEach { start ->
            val routeLength = totalSteps(map.route, map.nodes[start.name]!!)
            routeLengths[start.name] = routeLength
        }

        routeLengths.forEach { (node, length) ->
            println("Route from $node has length $length")
        }

        return routeLengths
    }

    /**
     * The LCM is the first number that is divisible by all the
     * values given, i.e. in this case, the point at which all looping
     * routes end in sync
     */
    fun leastCommonMultiple(values: List<Int>): Long {
        val lowest = values.min()
        var multiple = 1L

        while (true) {
            val checkValue = lowest * multiple
            if (values.all { checkValue % it == 0L }) break
            multiple++
        }

        return lowest * multiple
    }


    // Test data
    val testInput1 = readInput(2023, "Day08_test1")
    val testInput2 = readInput(2023, "Day08_test2")
    val testInput3 = readInput(2023, "Day08_test3")
    // For Part 1
    val testMap1 = parseMap(testInput1)
    val testMap2 = parseMap(testInput2)
    check(totalSteps(testMap1.route, testMap1.nodes[START]!!) == 2)
    check(totalSteps(testMap2.route, testMap2.nodes[START]!!) == 6)
    // For Part 2
    val testMap3 = parseMap(testInput3)
    // This works, but not for larger maps
    check(totalParallelSteps(testMap3) == 6L)
    // Alternative method for larger maps
    val testLengths = findRouteLengths(testMap3)
    check(leastCommonMultiple(testLengths.values.toList()) == 6L)

    // The real thing
    val input = readInput(2023, "Day08")
    // For Part 1
    val map = parseMap(input)
    totalSteps(map.route, map.nodes[START]!!).println()
    // For Part 2
    // Can't use this simplistic brute-force solution
    // totalParallelSteps(map).println()
    // So here is a different technique (see comments at top of file)
    val lengths = findRouteLengths(map)
    leastCommonMultiple(lengths.values.toList()).println()
}
