package service

import entity.*
/**
 * Service layer class that provides the logic for the possible actions a player
 * can take: playing card, swapping hand, drawing card or skip turn
 */
class PlayerService(private val rootService: RootService): AbstractRefreshingService() {

    /**
     * General Description
     * This method allows the player to take the card
     * from either the draw pile or the discard pile
     *
     * Preconditions:
     * The game must be started
     * The game must be not over
     * There must be a current player
     *
     * Post conditions:
     * Player has a card in hand
     * Selected card is removed from the pile it was previous in
     *
     * Parameters:
     * @param used: the Boolean parameter,
     * that shows whether the card should be taken from draw (false) or discard pile(true).
     *
     * Valid parameter ranges:
     * used: true/false
     *
     * Result:
     * This method does not return any value (Unit)
     *
     * Error cases:
     * IllegalStateException - If the game either hasn't started or is already over
     * IllegalArgumentException - The discard pile is empty
     */
    fun drawCard(used: Boolean) {
        val game = requireNotNull(rootService.kaboo) { "There is no running game!" }
        val currentPlayer = game.currentPlayer
        require(currentPlayer.viewedCards)
        require(currentPlayer.hand == null) {"Can't draw more while holding cards in hand! "}
        val drawnCard = if (used) {
            require(game.usedStack.isNotEmpty()) { "Can't draw from empty pile!" }
            game.usedStack.pop()
        }
        else {
            require(game.newStack.isNotEmpty()) { "Can't draw from empty pile!" }
            game.newStack.pop()
        }

        currentPlayer.hand = drawnCard
        val isPowerCard = drawnCard.value.toString() == "7" ||
                drawnCard.value.toString() == "8" ||
                drawnCard.value.toString() == "9" ||
                drawnCard.value.toString() == "10" ||
                drawnCard.value.toString() == "J" ||
                drawnCard.value.toString() == "Q"

        onAllRefreshables { refreshAfterDraw(discardable = !used, usablePower = (!used && isPowerCard)) }
    }

    /**
     * General Description:
     * The method discard the card in player's hand into the used Stack
     *
     * Preconditions:
     * The game must be started
     * The game must be not over
     * Current player must have a card in hand
     *
     * Postconditions:
     * Current player's hand card goes to discard pile
     * Current player has no card in hand anymore
     * The turn goes to another player by calling endTurn()
     *
     * Parameters:
     * None
     *
     * Valid parameter ranges:
     * Not applicable
     *
     * Result:
     * This method does not return any value (Unit)
     *
     * Error cases:
     * IllegalStateException - If the game either hasn't started or is already over
     * IllegalArgumentException - If the player got no card in his hand
     */
    fun disCard() {
        val game = requireNotNull(rootService.kaboo) { "There is no running game!" }
        val toDiscard = requireNotNull(game.currentPlayer.hand) { "No card to discard!" }

        game.usedStack.push(toDiscard)
        game.currentPlayer.hand = null

        onAllRefreshables { refreshAfterDiscard() }
        rootService.gameService.endTurn()
    }

    /**
     * General Description:
     * The Method swapSelf() gives the Player a way to swap
     * the Card in his hand with one Card in his deck
     * (the postion of the Card in the deck is given by the Parameter)
     *
     * Preconditions:
     * The currentplayer has drawn a card (hand is not empty)
     *
     * Postconditions:
     * The Card on the Position in currentPlayers Deck moved to top of the used stack
     * The Card in hand is placed in the given Position of currentPlayers deck
     * Call refreshAfterSwapSelf()
     *
     * Parameters:
     * @param position: chosen Position in currentPlayer Deck to swap Card in hand with
     *
     * Valid Parameters Range:
     * position: TOP_LEFT/TOP_RIGHT/BOTTOM_LEFT/BOTTOM_RIGHT
     *
     * Results:
     * This Method has no return (Unit)
     *
     * Error Cases:
     * IllegalArgumentException: position doesn't belong to enums [DeckPosition]
     * IllegalStateException: currentPlayer hasn't drawn a Card
     */
    fun swapSelf(position : DeckPosition) {
        val game = requireNotNull(rootService.kaboo) { "There is no running game!" }
        val hand = requireNotNull(game.currentPlayer.hand) { "No card on hand!" }

        game.usedStack.push(game.currentPlayer.deck[position.toInt()])
        game.currentPlayer.deck[position.toInt()] = hand
        game.currentPlayer.hand = null

        onAllRefreshables { refreshAfterSwapSelf(position = position) }
        rootService.gameService.endTurn()
    }

    /**
     * General Description:
     * The usePower() method is called when a player draws a power card
     * and wants to use its effect.The power card must have been drawn directly from the draw pile.
     * Depending on the value of the drawn card, a specific special effect is triggered:
     * Jack: Blindly swap with another player's card
     * Queen: View and/or swap own and opponent's cards
     * 7 or 8: View one of your own cards
     * 9 or 10: View one of the opponent’s cards
     * After the effect has been executed,
     * the power card is placed face-up on the discard pile.
     *
     * Preconditions:
     * An active game must be running.
     * The player has drawn a valid power card (Jack, Queen, 7, 8, 9, 10) from the draw pile.
     *
     * Postconditions:
     * The method refreshAfterPower() is called to update the UI.
     *
     * Parameters:
     * None
     *
     * Valid parameter ranges:
     * None
     *
     * Result:
     * This method does not return a value (Unit).
     *
     * Error cases:
     * IllegalStateException: Thrown if the player tries to use usePower() without having drawn a power card.
     */
    fun usePower() {
        val game = requireNotNull(rootService.kaboo) { "There is no running game!" }
        val hand = requireNotNull(game.currentPlayer.hand) { "No card on hand!" }
        val isPowerCard = hand.value.toString() == "7" ||
                hand.value.toString() == "8" ||
                hand.value.toString() == "9" ||
                hand.value.toString() == "10" ||
                hand.value.toString() == "J" ||
                hand.value.toString() == "Q"
        require(isPowerCard) {"This card has no power!"}

        val highlightDeckPlayer = hand.value.toString() == "7" ||
                hand.value.toString() == "8" ||
                hand.value.toString() == "Q"
        val highlightDeckOpponent = hand.value.toString() == "9" ||
                hand.value.toString() == "10" ||
                hand.value.toString() == "Q"


        onAllRefreshables { refreshAfterUsePower(highlightDeckPlayer, highlightDeckOpponent) }
    }

    /**
     * General Description:
     * The method peekCardPlayer() instructs the GUI to reveal the card
     * at the specified position in the deck of the given player.
     * Usecase : for first round or whenever using a power card with value from 7 to 10.
     *
     * Preconditions:
     * A game must be started ([Kaboo] != null and currentPlayer != null).
     * The game must not be finished yet.
     * The drawn card can be any power card except a Jack or Queen.
     *
     * Postconditions:
     * The refresh method refreshAfterPeekCardPlayer() is called to
     * display the revealed card on the GUI.
     * No card in hand.
     * Cards must be flipped back after being viewed
     * by calling endTurn() inside refreshAfterCardPlayer().
     *
     * Parameters:
     * @param positionToPeek: position in the deck of playerToPeek should be revealed.
     * @param playerToPeek: a Player, whose card at positionToPeek in their deck should be revealed.
     *
     * Valid Parameter Ranges:
     * @param positionToPeek: enum [DeckPosition].
     * @param playerToPeek: must be a player participating in the current game.
     *
     * Result:
     * This method does not return any value (Unit).
     *
     * Error Cases:
     * IllegalStateException: When the Game hasn't started yet or is already finished
     */
    fun peekCardPlayer(positionToPeek: DeckPosition, playerToPeek: Player) {
        val game = requireNotNull(rootService.kaboo) { "There is no running game!" }
        val currentPlayer = game.currentPlayer
        val opponent = if (game.currentPlayer == game.player1) game.player2 else game.player1

        if (game.currentPlayer.viewedCards) {
            val hand = requireNotNull(game.currentPlayer.hand) { "No card on hand!" }
            val allowToPeekOwn = (hand.value.toString() == "7" || hand.value.toString() == "8")
                    && playerToPeek == currentPlayer
            val allowToPeekOther = (hand.value.toString() == "9" || hand.value.toString() == "10")
                    && playerToPeek == opponent
            val allowedToPeek = allowToPeekOwn || allowToPeekOther

            require(allowedToPeek) {"This hand card has no power for peeking!"}
            game.usedStack.push(hand)
            game.currentPlayer.hand = null
        }

        // display button for endTurn()
        onAllRefreshables { refreshAfterPeekCardPlayer(positionToPeek, playerToPeek) }
    }

    /**
     * General Description:
     * The Method swapOther(): Unit swaps the Card from currentPlayer deck at ownPosition
     * with the Card from otherPlayer at otherPosition.
     *
     * Preconditions:
     * currentPlayer has drawn from new Stack
     * currentPlayer has a Jack or a Queen on hand
     *
     * Post conditions:
     * The Cards at ownPosition in currentPlayer Deck was swapped
     * with Card at otherPosition in otherPlayer Deck
     * The Card in hand is placed on top of used Card stack
     * Call refreshSwapOther() and end turn
     *
     * Parameters:
     * @param ownPosition: position in currentPlayer Deck to swap Cards with
     * @param otherPosition: position in otherPlayer Deck to swap Cards with
     *
     * Valid Parameters Range:
     * ownPosition: has to be part of the enum [DeckPosition]
     * otherPosition: has to be part of the enum [DeckPosition]
     *
     * Results:
     * This Method has no return (Unit)
     *
     * Error Cases:
     * IllegalArgumentException: ownPosition or otherPosition not in [DeckPosition]
     * IllegalStateException: currentPlayer hasn't drawn a new Card
     * currentPlayer hand isn't Queen or Jack
     */
    fun swapOther(ownPosition : DeckPosition, otherPosition : DeckPosition) {
        val game = requireNotNull(rootService.kaboo) { "There is no running game!" }
        val hand = requireNotNull(game.currentPlayer.hand) { "No card on hand!" }
        require(hand.value.toString() == "J" || hand.value.toString() == "Q")

        val ownDeck = game.currentPlayer.deck
        val otherDeck = if (game.currentPlayer == game.player1) game.player2.deck else game.player1.deck
        val temp = ownDeck[ownPosition.toInt()]
        ownDeck[ownPosition.toInt()] = otherDeck[otherPosition.toInt()]
        otherDeck[otherPosition.toInt()] = temp

        game.usedStack.push(hand)
        game.currentPlayer.hand = null

        onAllRefreshables { refreshAfterSwapOther(ownPosition = ownPosition, otherPosition = otherPosition) }
        rootService.gameService.endTurn()
    }

    /**
     * General Description:
     * This method is called, whenever a player chooses a card to swap when having a jack or queen.
     * It stores the chosen card in either the ownSelected or the otherSelected variable of the current player,
     * can't be undone by choosing a chosen card again.
     * When both variables (ownSelected and otherSelected) are set:
     * If the current hand card is a Queen it calls the refreshAfterChooseCard()
     * to reveal 2 cards and to show the options swap or not.
     * If the current hand card is a Jack, it calls the swapOther()
     * to swap the selected cards immediately.
     *
     * Preconditions:
     * The Game must be started and is not over
     * The Card must be a Jack or Queen.
     *
     * Postconditions:
     * The chosen card is stored in the ownSelected or otherSelected variable of the currentPlayer.
     * If both variables are set and the current hand card is a Queen,
     * the refreshAfterChooseCard method is called to show the swap button.
     * If the current hand card is a Jack,
     * the swapOther() is called to swap the selected cards immediately.
     *
     * Parameters:
     * @param chosenCardPosition: DeckPosition: The position in the Deck of the chosen card for swapping.
     * @param chosenPlayer: Player:A Player that is part of the game.
     *
     * Valid parameter ranges:
     * chosenCardPosition must be a valid position in the Deck, corresponding to the card the player wants to swap.
     *
     * Result:
     * The method doesn’t return any value (unit)
     *
     * Error cases:
     * IllegalStateException : If the game hasn’t been started or is over.
     * IllegalStateException: If ownSelected and otherSelected are both set.
     * IllegalStateException: If the Card is not a Jack or Queen.
     * IllegalArgumentException: If the cardOfPlayer is not Part of the game.
     * IllegalArgumentException: If the currentPlayer already selected a card of the cardOfPlayer deck.
     * IllegalArgumentException: If the cardOfPlayer has no card at the specified position in the deck.
     */
    fun chooseCard(chosenCardPosition : DeckPosition, chosenPlayer : Player) {
        val game = requireNotNull(rootService.kaboo) { "There is no running game!" }
        val currentPlayer = game.currentPlayer
        val hand = requireNotNull(currentPlayer.hand) { "No card on hand!" }
        require(hand.value.toString() == "J" || hand.value.toString() == "Q")

        if (chosenPlayer == currentPlayer) {
            require(currentPlayer.ownSelected == null)
            currentPlayer.ownSelected = chosenCardPosition
        }
        else {
            require(currentPlayer.otherSelected == null)
            currentPlayer.otherSelected = chosenCardPosition
        }

        val ownPosition = currentPlayer.ownSelected
        val otherPosition = currentPlayer.otherSelected
        if (ownPosition != null && otherPosition != null) {
            if (hand.value.toString() == "J") {
                swapOther(ownPosition, otherPosition)
            }
            else {
                // swap or not - button, yes -> swapOther(), no -> endTurn()
                onAllRefreshables { refreshAfterChooseCard() }
            }
        }
    }

    /**
     * General Description:
     * This method is called after a player knocks. Once a player has knocked,
     * the other player is no longer allowed to knock and only has one final turn.
     *
     * Preconditions:
     * An active game must be running.
     * No other player has knocked yet.
     *
     * Postconditions:
     * The knock button is disabled.
     * A suitable log message is created.
     * The method refreshAfterKnock() is called.
     * The current player's turn ends.
     * The game enters the final round.
     *
     * Parameters:
     * None
     *
     * Valid parameter ranges:
     * None
     *
     * Result:
     * This method does not return a value (Unit).
     *
     * Error cases:
     * IllegalStateException: Thrown if another player has already knocked.
     */
    fun knock() {
        val game = requireNotNull(rootService.kaboo) { "There is no running game!" }
        require(game.currentPlayer.viewedCards)
        require(!game.player1.knocked && !game.player2.knocked) {"Can't knock"}
        game.currentPlayer.knocked = true
        rootService.gameService.endTurn()
    }
}

