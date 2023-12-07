/**
 * DAY 04 - Scratch Cards
 *
 * Fun one with need for recursion for second part
 * since winning cards earn extra cards, which in
 * turn can earn extra cards and so on.
 */

package aoc2023

import utils.println
import utils.readInput
import kotlin.math.pow

data class Card(
    val winNums: List<Int>,
    val playNums: List<Int>
) {
    var matchNums = mutableListOf<Int>()

    override fun toString(): String {
        return "CARD...\n  win: $winNums \n  play: $playNums \n match: $matchNums"
    }
}

fun main() {
    /**
     * Parse the given card list, identifying:
     * - card ID
     * - list of winning numbers
     * - list of scratched off numbers
     * - list of matching numbers
     * - score, based on number of matches
     */
    fun parseCards(cardList: List<String>): MutableMap<Int, Card> {
        val cards = mutableMapOf<Int, Card>()
        val whitespaces = "\\s+".toRegex()

        for (cardData in cardList) {
            val cardInfo = cardData.split(": ")
            val cardID = cardInfo[0].substring(5).trim().toInt()
            val cardNumbers = cardInfo[1].split(" | ")
            val winningNumbers = cardNumbers[0]
                .trim()
                .split(whitespaces)
                .map { it -> it.toInt() }
            val playNumbers = cardNumbers[1]
                .trim()
                .split(whitespaces)
                .map { it -> it.toInt() }

            val card = Card(winningNumbers, playNumbers)

            for (number in playNumbers) {
                if (number in winningNumbers) {
                    card.matchNums.add(number)
                }
            }

            cards[cardID] = card
        }

        return cards
    }

    /**
     * Add up the points per card
     */
    fun sumWinningPoints(cards: Map<Int, Card>): Int {
        var total = 0
        for (card in cards.values) {
//            println(card)
            val matchCount = card.matchNums.size
            if (matchCount > 0) {
                val points = (2.0).pow(matchCount - 1).toInt()
                total += points
//                println("points: $points")
            }
        }
        return total
    }

    /**
     * Count the size of the winning stack of cards
     * after all the additional bonus cards have
     * been accounted for
     */
    fun countWinningCards(cards: Map<Int, Card>): Int {
        val cardList = mutableListOf<Int>()
        val maxID = cards.keys.last()

        /**
         * Recursively add cards to winning card list
         */
        fun addToList(id: Int) {
            cardList.add(id)

            val card = cards[id]!!
            val matchCount = card.matchNums.size

            // Does this card have wins, and not at end?
            if (matchCount > 0 && id < maxID) {
                // Yep, so see what extra cards we've won
                val bonusStart = id + 1
                val bonusEnd = if (id + matchCount <= maxID) id + matchCount else maxID
                // And add them in too
                for (i in bonusStart..bonusEnd) {
                    addToList(i)
                }
            }
        }

        // Work through all original cards, adding to win list
        for (id in cards.keys) {
            addToList(id)
        }

        return cardList.size
    }


    // Test data
    val testInput = readInput(2023, "Day04_test")
    val testCards = parseCards(testInput)
    check(sumWinningPoints(testCards) == 13)
    check(countWinningCards(testCards) == 30)

    // Real thing
    val input = readInput(2023, "Day04")
    val cards = parseCards(input)
    sumWinningPoints(cards).println()
    countWinningCards(cards).println()
}
