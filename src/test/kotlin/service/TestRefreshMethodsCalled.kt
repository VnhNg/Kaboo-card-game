package service

import entity.DeckPosition
import entity.Player

/**
 * A [Refreshable] returns true
 * if a refresh method has been called (since last [reset])
 */
class TestRefreshMethodsCalled(): Refreshable {


    var refreshAfterGameStartCalled: Boolean = false
    var refreshAfterEndGameCalled: Boolean = false
    var refreshAfterDrawCalled: Boolean = false
    var refreshAfterDiscardCalled: Boolean = false
    var refreshAfterSwapOtherCalled: Boolean = false
    var refreshAfterSwapSelfCalled: Boolean = false
    var refreshAfterPeekCardPlayerCalled: Boolean = false
    var refreshAfterEachTurnCalled: Boolean = false
    var refreshAfterKnockCalled: Boolean = false
    var refreshAfterGameMoveCalled: Boolean = false
    var refreshAfterUsePowerCalled: Boolean = false
    var refreshAfterChooseCardCalled: Boolean = false


    /**
     * Resets all called properties to false
     */
    fun reset() {
        refreshAfterGameStartCalled = false
        refreshAfterEndGameCalled = false
        refreshAfterDrawCalled = false
        refreshAfterDiscardCalled = false
        refreshAfterSwapOtherCalled = false
        refreshAfterSwapSelfCalled = false
        refreshAfterEachTurnCalled = false
        refreshAfterKnockCalled = false
        refreshAfterGameMoveCalled = false
        refreshAfterUsePowerCalled = false
        refreshAfterChooseCardCalled = false
    }

    override fun refreshAfterGameStart() {
        refreshAfterGameStartCalled = true
    }

    override fun refreshAfterEndGame(winnerMessage: String) {
        refreshAfterEndGameCalled = true
    }

    override fun refreshAfterDraw(discardable: Boolean, usablePower: Boolean) {
        refreshAfterDrawCalled = true
    }

    override fun refreshAfterDiscard() {
        refreshAfterDiscardCalled = true
    }

    override fun refreshAfterSwapOther(ownPosition: DeckPosition, otherPosition: DeckPosition) {
        refreshAfterSwapOtherCalled = true
    }

    override fun refreshAfterSwapSelf(position: DeckPosition) {
        refreshAfterSwapSelfCalled = true
    }

    override fun refreshAfterPeekCardPlayer(positionToPeak: DeckPosition, playerToPeak: Player) {
        refreshAfterPeekCardPlayerCalled = true
    }

    override fun refreshAfterEachTurn() {
        refreshAfterEachTurnCalled = true
    }

    override fun refreshAfterGameMove(canKnock: Boolean, canTakeUsedCard: Boolean) {
        refreshAfterGameMoveCalled = true
    }

    override fun refreshAfterUsePower(highlightDeckPlayer1: Boolean, highlightDeckPlayer2: Boolean) {
        refreshAfterUsePowerCalled = true
    }

    override fun refreshAfterChooseCard() {
        refreshAfterChooseCardCalled = true
    }
}
