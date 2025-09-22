package service

import entity.*
import entity.CardValue.*
import tools.aqua.bgw.util.Stack

/**
 * Service layer class that provides the logic for actions not directly
 * related to a single player.
 */
class GameService(private val rootService: RootService): AbstractRefreshingService() {

    /**
     * method creates a 52-card deck made of all combinations of the four “CardSuit” and thirteen “CardValue”.
     * The resulting deck is shuffled and returned as a Stack<Card>.
     */
    private fun createDeck(): Stack<Card>{

        val cardsCollection = CardSuit.entries.flatMap {suit ->
            CardValue.entries.map { value ->
                Card(suit, value)
            }
        }
        val deck = Stack(cardsCollection)
        deck.shuffle()
        return deck
    }

    /**
     * The methode giveStartCards(): Unit fills each players deck with four cards from the deck.
     */
    private fun giveStartCards() {
        val game = requireNotNull(rootService.kaboo) {"Start a game to create deck!"}
        require(game.player1.deck.isEmpty()) {"Player 1 already has some cards."}
        val deck1: MutableList<Card> = game.player1.deck
        require(game.player2.deck.isEmpty()) {"Player 2 already has some cards."}
        val deck2: MutableList<Card> = game.player2.deck
        require(game.newStack.size == 52) {"Deck not valid!"}
        val newStack: Stack<Card> = game.newStack

        repeat(times = 4) {
            game.run{
                deck1.add(newStack.pop())
                deck2.add(newStack.pop())
            }
        }
    }

    /**
     * General description:
     * The Methode startGame(): Unit allows the players to start the game.
     * It takes the names of the players to create two player,
     * creates a deck of Cards and gives the start cards to the players.
     *
     * Preconditions:
     * There must not be a [Kaboo] instance.
     *
     * Post conditions:
     * Game is started, by creating a [Kaboo] instance
     * Creates two players
     * Uses createDeck(): Stack<Card> and giveStartCards(): Unit
     * Calls the refresh methode refreshAfterGameStart(): Unit
     *
     * Parameters:
     * @param player1
     * @param player2
     *
     * Result:
     * The methode does not have a return value (Unit).
     *
     * Error Cases:
     * IllegalStateException: If the game has already been started.
     */
    fun startGame(player1: String, player2: String) {
        require(rootService.kaboo == null) {"There is another game running!"}

        // Create 2 player instances
        require(player1 != player2) {"Names of 2 players must be different!"}
        val player1 = Player(player1)
        val player2 = Player(player2)
        // Create Kaboo instance, set up and assign the first player
        val firstPlayer = if ((0..1).random() == 0) player1 else player2
        val game = Kaboo(player1 = player1, player2 = player2, currentPlayer = firstPlayer)
        rootService.kaboo = game
        game.newStack.pushAll(createDeck().popAll())
        giveStartCards()

        println("startGame-Stack peek: "+game.newStack.peek().toString())
        onAllRefreshables { refreshAfterGameStart() }
    }

    /**
     * get score for a card
     */
    private fun getScore(card: Card): Int {
        return when(card.value) {
            TWO -> 2
            THREE -> 3
            FOUR -> 4
            FIVE -> 5
            SIX -> 6
            SEVEN -> 7
            EIGHT -> 8
            NINE -> 9
            TEN -> 10
            JACK -> 10
            QUEEN -> 10
            KING -> -1
            ACE -> 1
        }
    }

    /**
     * calculate score for a player
     */
    private fun scoreCalculate(player: Player): Int {
        var score = 0
        for (card in player.deck) { score += getScore(card) }
        return score
    }

    /**
     * General Description:
     * In the method endGame(): String each player's score will be calculated
     * and the winner will be determined.
     * After that refreshAfterEndGame(winnerMessage: String)
     * will be called to pop up a window showing the winner,
     * and allowing to start new game.
     *
     * Preconditions:
     * The current player had already knocked.
     * or no more cards left in the newCardStack.
     *
     * Post conditions:
     * refreshAfterEndGame(winnerMassage: String) will be called.
     * reset game = null
     *
     * Parameters:
     * None.
     *
     * Valid Parameter Ranges:
     * Not applicable.
     *
     * Result:
     * The method returns the String name of the winner.
     *
     * Error Cases:
     * IllegalStateException: When there are still cards left in the newCardStack
     * and the current player didn't knock in his previous turn.
     */
    fun endGame() {
        val game = requireNotNull(rootService.kaboo) {"There is no running game!"}
        require(game.currentPlayer.knocked or game.newStack.isEmpty())

        //Calculate Score
        val player1 = game.player1
        val player2 = game.player2
        val score1 = scoreCalculate(player1)
        val score2 = scoreCalculate(player2)

        val massage = when {
            score1 < score2 -> player1.name
            score1 > score2 -> player2.name
            else -> "It's a tie!"
            }

        onAllRefreshables { refreshAfterEndGame(massage) }
        rootService.kaboo = null
    }

    /**
     * General Description:
     * The method peekCardsFirstRound() calls the method peekCardPlayer() twice
     * using indices for the bottom two cards and the current player as arguments.
     *
     * Preconditions:
     * A game must be started (Kaboo != null and currentPlayer != null).
     * The game must not be finished yet.
     * There must be a current player.
     * The current player's deck must be filled.
     * It is still the first round, and the current player has not yet seen their bottom cards.
     *
     * Postconditions:
     * The method peekCardPlayer() is called twice to reveal cards.
     *
     * Parameters:
     * None.
     *
     * Valid Parameter Ranges:
     * Not applicable.
     *
     * Result:
     * This method does not return any value (Unit).
     *
     * Error Cases:
     * IllegalStateException: When the Game is already finished.
     */
    private fun peekCardsFirstRound() {
        val game = requireNotNull(rootService.kaboo) { "There is no running game!" }
        rootService.playerService.peekCardPlayer(DeckPosition.BOTTOM_LEFT, game.currentPlayer)
        rootService.playerService.peekCardPlayer(DeckPosition.BOTTOM_RIGHT, game.currentPlayer)
    }

    /**
     * General Description:
     * The method gameMove()is called at very start of each turn
     * (right after the next player takes over).
     * If the player in the first turn
     * If the current player had knocked in his previous turn or the newCardStack is empty,
     * endGame() will be called.
     * Otherwise, it allows the current player to execute one of common playing actions.
     *
     * Preconditions:
     * The game must be started
     * The game must be not over
     * There must be a current player
     *
     * Post conditions:
     * endGame() is called if its conditions are met.
     *
     * Parameters:
     * None.
     *
     * Valid Parameter Ranges:
     * Not applicable.
     *
     * Result:
     * This method does not return any value (Unit).
     *
     * Error Cases:
     * IllegalStateException: When the game hasn't started or is over.
     * IllegalStateException: When the next player hasn't taken over.
     */
    fun gameMove() {
        val game = requireNotNull(rootService.kaboo) {"There is no running game!"}
        if (!game.currentPlayer.viewedCards) {
            peekCardsFirstRound()
        }
        if(game.currentPlayer.knocked or game.newStack.isEmpty()) {
            endGame()
        }
        else {
            val canKnock = !(game.player1.knocked || game.player2.knocked)

            // Disable other actions in first round
            onAllRefreshables {
                refreshAfterGameMove(
                    canKnock = canKnock,
                    canTakeUsedCard = game.usedStack.isNotEmpty()
                )
            }
        }
    }

    /**
     *General Description:
     * The endTurn() method finalizes the current player's turn,
     * advances the game to the next player and
     * refreshes the UI to pop a Confirm-Screen for the next player up.
     *
     * Preconditions:
     * The Game must be INITIALIZED or ACTIVE
     *
     * Post conditions:
     * currentPlayer is updated to the next player
     * Calls refreshAfterEachTurn() to update the UI
     *
     * Parameters:
     * none
     *
     * Valid parameter ranges:
     * Not applicable.
     *
     * Result:
     * It doesn’t return any value (Unit)
     *
     * Error cases:
     * IllegalStateException : If the game hasn’t been INITIALIZED or isn’t ACTIVE anymore
     */

    fun endTurn() {
        val game = requireNotNull(rootService.kaboo) {"There is no running game!"}

        game.currentPlayer.viewedCards = true
        // reset swapping property
        game.currentPlayer.ownSelected = null
        game.currentPlayer.otherSelected = null
        // reset hand card
        game.currentPlayer.hand = null

        // switch player
        game.currentPlayer = if(game.currentPlayer == game.player1) game.player2 else game.player1

        onAllRefreshables { refreshAfterEachTurn() }
    }
}

