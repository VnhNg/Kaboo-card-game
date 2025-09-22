package service

import entity.*
/**
 * This interface provides a mechanism for the service layer classes to communicate
 * (usually to the GUI classes) that certain changes have been made to the entity
 * layer, so that the user interface can be updated accordingly.
 *
 * Default (empty) implementations are provided for all methods, so that implementing
 * GUI classes only need to react to events relevant to them.
 *
 * @see AbstractRefreshingService
 */
interface Refreshable {

    /**
     * BoardGameScene, NextPlayerMenuScene
     */
    fun refreshAfterGameStart() {}


    /**
     * BoardGameScene, EndGameMenuScene
     */
    fun refreshAfterEndGame(winnerMassage: String) {}

    /**
     * BoardGameScene,
     */
    fun refreshAfterDraw(discardable: Boolean, usablePower: Boolean) {}

    /**
     *  BoardGameScene,
     */
    fun refreshAfterDiscard() {}

    /**
     * BoardGameScene,
     */
    fun refreshAfterSwapOther(ownPosition: DeckPosition, otherPosition: DeckPosition) {}

    /**
     *  BoardGameScene,
     */
    fun refreshAfterSwapSelf(position: DeckPosition){}

    /**
     * BoardGameScene,
     */
    fun refreshAfterPeekCardPlayer(positionToPeak:DeckPosition, playerToPeak: Player) {}

    /**
     *  BoardGameScene, NextPlayerMenuScene
     */
    fun refreshAfterEachTurn() {}

    /**
     *  BoardGameScene
     */
    fun refreshAfterGameMove(canKnock: Boolean, canTakeUsedCard: Boolean) {}

    /**
     *  BoardGameScene
     */
    fun refreshAfterUsePower(highlightDeckPlayer: Boolean, highlightDeckOpponent: Boolean) {}

    /**
     * BoardGameScene,
     */
    fun refreshAfterChooseCard() {}

}

