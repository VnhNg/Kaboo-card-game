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
 * Test swapOther()
 */
class SwapOtherTest {
    var rootService = RootService()
    var testRefreshMethodsCalled = TestRefreshMethodsCalled().apply {
        rootService.addRefreshables(this)
    }

    /**
     * Specific player1 as currentPlayer
     * Give each players 1 card for testing swapping
     */
    @BeforeTest
    fun setUp() {
        rootService.gameService.startGame("X", "Y")
        val game = rootService.kaboo
        assertNotNull(game)
        game.currentPlayer = game.player1
        val nineOfSpades = Card(CardSuit.SPADES, CardValue.NINE)
        game.player1.deck[0] = nineOfSpades
        val kingOfClubs = Card(CardSuit.CLUBS, CardValue.KING)
        game.player2.deck[3] = kingOfClubs
    }

    @Test
    fun testSwap() {
        val game = rootService.kaboo
        assertNotNull(game)
        val nineOfSpades = Card(CardSuit.SPADES, CardValue.NINE)
        val kingOfClubs = Card(CardSuit.CLUBS, CardValue.KING)
        game.player1.hand = Card(CardSuit.HEARTS, CardValue.JACK)
        assertDoesNotThrow {
            rootService.playerService.chooseCard(DeckPosition.TOP_LEFT, game.player1)
        }
        assertFalse { testRefreshMethodsCalled.refreshAfterSwapOtherCalled }

        game.player1.ownSelected = DeckPosition.TOP_LEFT
        assertThrows<IllegalArgumentException> {
            rootService.playerService.chooseCard(DeckPosition.BOTTOM_LEFT, game.player1)
        }
        assertFalse { testRefreshMethodsCalled.refreshAfterSwapOtherCalled }

        rootService.playerService.chooseCard(DeckPosition.BOTTOM_RIGHT, game.player2)
        assertEquals(kingOfClubs ,game.player1.deck[0])
        assertEquals(nineOfSpades ,game.player2.deck[3])
        assertEquals(Card(CardSuit.HEARTS, CardValue.JACK) ,game.usedStack.peek())
        assertTrue { testRefreshMethodsCalled.refreshAfterSwapOtherCalled }
    }

}