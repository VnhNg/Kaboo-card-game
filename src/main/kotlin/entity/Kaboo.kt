package entity

import tools.aqua.bgw.util.Stack
/**
 * Represents a Kaboo game.
 *
 * @param player1 One player of the game
 * @param player2 The other player of the game
 * @param currentPlayer The player in turn
 *
 * @property newStack The stack of cards that can be drawn
 * @property usedStack The stack of cards that have been played
 */

class Kaboo(
    val player1: Player,
    val player2: Player,
    var currentPlayer: Player
){
    val newStack: Stack<Card> = Stack()
    val usedStack: Stack<Card> = Stack()
    val log: MutableList<String> = mutableListOf()
}



