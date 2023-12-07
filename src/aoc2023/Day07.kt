/**
 * DAY 07 - Camel Cards
 *
 * Well, that was pretty tricky. Not really sure that my
 * hand-checking is the best - certainly would get ugly if
 * the hands had more than 5 cards. But it works
 *
 * For Part 2, the wildcards took a bit of puzzling out
 * before I realised that I only had to sub in each of the
 * non-wildcard values for *all* wildcards in one go
 */

package aoc2023

import utils.println
import utils.readInput

/**
 * Used to rank the hand types
 */
enum class HandType(val description: String) {
    NONE("Nothing"),
    HIGHCARD("High Card"),
    ONEPAIR("One Pair"),
    TWOPAIRS("Two Pairs"),
    THREEOFKIND("Three of a Kind"),
    FULLHOUSE("Full House"),
    FOURKIND("Four of a Kind"),
    FIVEOFKIND("Five of a Kind"),
}

// Part 1 deck
val deckNormal = "23456789TJQKA".toList()

// Part 2 deck with wildcard J
val deckWild = "J23456789TQKA".toList()
const val WILD = 'J'

/**
 * Extension to check if the section of a given
 * string between start and end inclusize contains
 * all the same character
 */
fun String.allSame(start: Int, end: Int): Boolean {
    val text = this.substring(start..end)
    val char = text.first()
    text.forEach {
        if (it != char) return false
    }
    return true
}

fun main() {
    class Hand(
        val hand: String,   // The given hand
        val bid: Int,       // The bid placed
        wild: Char? = null  // Can supply a wildcard char
    ) {
        val cards = hand.toList()   // The card hand as a list
        val type: HandType          // The hand type, determined below

        init {
            // If no wild card, then simply process the hand as is
            if (wild == null) {
                type = type(cards)
            }
            // Otherwise we need to try out all ways of using the wildcard
            else {
                // What other cards to we have
                val nonWild = cards.filter { it != wild }

                // None? Then must be all wildcards
                if (nonWild.isEmpty()) {
                    type = HandType.FIVEOFKIND
                }
                // We have some, so sub in wildcard for each in turn and find best
                else {
                    var bestType = HandType.NONE
                    nonWild.forEach { char ->
                        val testCards = cards.map { if (it == wild) char else it }
                        val testType = type(testCards)
                        if (testType > bestType) bestType = testType
                    }
                    type = bestType
                }
            }
        }

        /**
         * Given a set of cards, find what type they are
         * Done by sorting first, then checking combinations from
         * best (5 of a kind) downwards
         *
         * Bit clunky... Must be a better way!
         */
        fun type(cards: List<Char>): HandType {
            val sorted = cards.sorted().joinToString("")

            return when {
                sorted.allSame(0, 4) -> HandType.FIVEOFKIND

                sorted.allSame(0, 3) ||
                        sorted.allSame(1, 4) -> HandType.FOURKIND

                (sorted.allSame(0, 2) && sorted.allSame(3, 4)) ||
                        (sorted.allSame(0, 1) && sorted.allSame(2, 4)) -> HandType.FULLHOUSE

                sorted.allSame(0, 2) ||
                        sorted.allSame(1, 3) ||
                        sorted.allSame(2, 4) -> HandType.THREEOFKIND

                (sorted.allSame(0, 1) && sorted.allSame(2, 3)) ||
                        (sorted.allSame(0, 1) && sorted.allSame(3, 4)) ||
                        (sorted.allSame(1, 2) && sorted.allSame(3, 4)) -> HandType.TWOPAIRS

                sorted.allSame(0, 1) ||
                        sorted.allSame(1, 2) ||
                        sorted.allSame(2, 3) ||
                        sorted.allSame(3, 4) -> HandType.ONEPAIR

                else -> HandType.HIGHCARD
            }
        }

        override fun toString(): String {
            return "$hand (${type.description}), $bid"
        }
    }

    /**
     * Run thru all hands and bids
     */
    fun parseHands(handList: List<String>, wild: Char? = null): MutableList<Hand> {
        val hands = mutableListOf<Hand>()
        for (handData in handList) {
            val (handCards, bid) = handData.split(" ")
            hands.add(Hand(handCards, bid.toInt(), wild))
        }
        return hands
    }

    /**
     * Sort the hands into rank order (via a bit of a gross
     * sorting chain) so we can calc the winnings (rank * bid)
     */
    fun totalWinnings(hands: MutableList<Hand>, deck: List<Char>): Int {
        hands.sortWith(
            compareBy { it: Hand -> it.type }
                .thenBy { deck.indexOf(it.cards[0]) }
                .thenBy { deck.indexOf(it.cards[1]) }
                .thenBy { deck.indexOf(it.cards[2]) }
                .thenBy { deck.indexOf(it.cards[3]) }
                .thenBy { deck.indexOf(it.cards[4]) }
        )

        var total = 0
        hands.forEachIndexed { i, hand ->
            val rank = i + 1
            val winnings = rank * hand.bid
            println("$hand x $rank -> $winnings")
            total += winnings
        }

        return total
    }


    // Test data
    val testInput = readInput(2023, "Day07_test")
    // For Part 1
    val testHands1 = parseHands(testInput)
    check(totalWinnings(testHands1, deckNormal) == 6440)
    // For Part 2
    val testHands2 = parseHands(testInput, WILD)
    check(totalWinnings(testHands2, deckWild) == 5905)

    // The real thing
    val input = readInput(2023, "Day07")
    // For Part 1
    val hands1 = parseHands(input)
    totalWinnings(hands1, deckNormal).println()
    // For Part 2
    val hands2 = parseHands(input, WILD)
    totalWinnings(hands2, deckWild).println()
}
