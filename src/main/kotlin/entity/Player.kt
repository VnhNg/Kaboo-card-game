package entity

/**
 * Entity to represent a player in the game "Up and Down".
 * @param name: name of player
 * @property knocked: did the player knock?
 * @property viewedCards: did the player see 2 last cards in his own deck (in first turn)?
 * @property deck: 4 deck cards
 * @property hand: drawn cards in player's hand
 * @property ownSelected: A chosen position in the player's [deck]
 * @property otherSelected: A chosen position in the opponent's [deck]
 */

class Player(val name: String) {
    var knocked: Boolean = false
    var viewedCards: Boolean = false
    val deck: MutableList<Card> = mutableListOf()
    var hand: Card? = null
    var ownSelected: DeckPosition? = null
    var otherSelected: DeckPosition? = null
}


