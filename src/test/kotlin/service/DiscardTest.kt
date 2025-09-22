package service

import entity.Card
import entity.CardSuit
import entity.CardValue
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

/**
 * Test discard()
 */
class DiscardTest {
    var rootService = RootService()
    var testRefreshMethodsCalled = TestRefreshMethodsCalled().apply {
        rootService.addRefreshables(this)
    }

    /**
     * Start the game and assign player 1 as currentPlayer, who is able to discard
     */
    @BeforeTest
    fun setUp() {
        rootService.gameService.startGame("X", "Y")
        val game = rootService.kaboo
        assertNotNull(game)
        game.currentPlayer = game.player1
    }

    /**
     * Current player's hand card goes to discard pile
     * Current player has no card in hand anymore
     * The turn goes to another player by calling endTurn()
     */
    @Test
    fun testValidDiscard() {
        val game = rootService.kaboo
        assertNotNull(game)
        val testCard = Card(CardSuit.SPADES, CardValue.ACE)
        game.player1.hand = testCard
        assertDoesNotThrow { rootService.playerService.disCard() }
        assertNull(game.player1.hand)
        assertEquals(testCard, game.usedStack.peek())
        assertEquals(game.player2, game.currentPlayer)
        assertTrue { testRefreshMethodsCalled.refreshAfterDiscardCalled }
    }

}