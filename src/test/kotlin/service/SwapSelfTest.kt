package service

import entity.Card
import entity.CardSuit
import entity.CardValue
import entity.DeckPosition
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import tools.aqua.bgw.util.Stack
import kotlin.test.*

/**
 * Test swapSelf()
 */
class SwapSelfTest {
    var rootService = RootService()
    var testRefreshMethodsCalled = TestRefreshMethodsCalled().apply {
        rootService.addRefreshables(this)
    }

    /**
     * The Card on the Position in currentPlayers Deck moved to top of the used stack
     * The Card in hand is placed in the given Position of currentPlayers deck
     */
    @Test
    fun testSwapSelf() {
        rootService.gameService.startGame("X", "Y")
        val game = rootService.kaboo
        assertNotNull(game)
        game.currentPlayer = game.player1
        game.player1.hand = Card(CardSuit.CLUBS, CardValue.FIVE)
        val thrownCard = game.player1.deck[1]

        // Valid for first round - Peak 2 own bottom cards
        assertDoesNotThrow {rootService.playerService.swapSelf(DeckPosition.TOP_RIGHT)}
        assertEquals(game.player1.deck[1], Card(CardSuit.CLUBS, CardValue.FIVE))
        assertEquals(thrownCard, game.usedStack.peek())
        assertTrue { testRefreshMethodsCalled.refreshAfterSwapSelfCalled }
    }
}