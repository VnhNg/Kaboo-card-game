package service

import entity.Card
import entity.CardSuit
import entity.CardValue
import entity.DeckPosition
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * Test peekCardPlayer()
 */
class PeekCardPlayerTest {
    var rootService = RootService()
    var testRefreshMethodsCalled = TestRefreshMethodsCalled().apply {
        rootService.addRefreshables(this)
    }

    /**
     * Start the game where both players have seen their own bottom cards
     * and specific player1 as currentPlayer
     */
    @BeforeTest
    fun setUp() {
        rootService.gameService.startGame("X", "Y")
        val game = rootService.kaboo
        assertNotNull(game)
        game.currentPlayer = game.player1

        // Valid for first round - Peak 2 own bottom cards
        assertDoesNotThrow {rootService.playerService.peekCardPlayer(DeckPosition.TOP_LEFT, game.player1)}
        game.player1.viewedCards = true
        game.player2.viewedCards = true
    }

    /**
     * Invalid card
     */
    @Test
    fun testInvalidCard() {
        val game = rootService.kaboo
        assertNotNull(game)
        game.player1.hand = Card(CardSuit.HEARTS, CardValue.THREE)

        assertThrows<IllegalArgumentException> {
            rootService.playerService.peekCardPlayer(DeckPosition.TOP_LEFT, game.player1)
        }
    }

    /**
     * Card value equals 7 or 8
     */
    @Test
    fun test7Or8() {
        val game = rootService.kaboo
        assertNotNull(game)
        game.player1.hand = Card(CardSuit.HEARTS, CardValue.EIGHT)

        assertDoesNotThrow { rootService.playerService.peekCardPlayer(DeckPosition.TOP_LEFT, game.player1) }
        assertEquals(Card(CardSuit.HEARTS, CardValue.EIGHT), game.usedStack.peek())
        assertTrue { testRefreshMethodsCalled.refreshAfterPeekCardPlayerCalled }

        testRefreshMethodsCalled.reset()
        assertThrows<IllegalArgumentException> {
            rootService.playerService.peekCardPlayer(DeckPosition.TOP_LEFT, game.player2)
        }
        assertTrue { testRefreshMethodsCalled.refreshAfterPeekCardPlayerCalled }
    }

    /**
     * Card value equals 9 or 10
     */
    @Test
    fun testValidPeek() {
        val game = rootService.kaboo
        assertNotNull(game)
        game.player1.hand = Card(CardSuit.HEARTS, CardValue.TEN)

        assertThrows<IllegalArgumentException> {
            rootService.playerService.peekCardPlayer(DeckPosition.TOP_LEFT, game.player1)
        }
        assertDoesNotThrow {rootService.playerService.peekCardPlayer(DeckPosition.TOP_LEFT, game.player2)}
        assertEquals(Card(CardSuit.HEARTS, CardValue.TEN), game.usedStack.peek())
    }
}