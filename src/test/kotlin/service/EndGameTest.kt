package service

import entity.Card
import entity.CardSuit
import entity.CardValue
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import tools.aqua.bgw.util.Stack
import kotlin.test.*

/**
 * Test endGame()
 */
class EndGameTest {
    var rootService = RootService()
    var testRefreshMethodsCalled = TestRefreshMethodsCalled().apply {
        rootService.addRefreshables(this)
    }

    /**
     * Simply start the game
     */
    @BeforeTest
    fun setUp(){
        rootService.gameService.startGame("X", "Y")
        val game = requireNotNull(rootService.kaboo)
    }

    /**
     * Noone has knocked and the stack of new card is not empty
     */
    @Test
    fun testInvalidEndGame() {
        assertThrows<IllegalArgumentException> { rootService.gameService.endGame() }
        }
    /**
     * Test endGame because of empty stack of new card
     */
    @Test
    fun testNoNewCard() {
        val game = requireNotNull(rootService.kaboo)
        game.newStack.popAll()
        assertDoesNotThrow { rootService.gameService.endGame() }
        assertTrue { testRefreshMethodsCalled.refreshAfterEndGameCalled }
    }

    /**
     * Test endGame because a player has knocked
     */
    @Test
    fun testKnocked() {
        val game = requireNotNull(rootService.kaboo)
        game.newStack.push(Card(CardSuit.CLUBS, CardValue.TEN))
        game.currentPlayer.knocked = true
        assertDoesNotThrow { rootService.gameService.endGame() }
        assertTrue { testRefreshMethodsCalled.refreshAfterEndGameCalled }
    }

    /**
     * Return massage: String to be tested
    @Test
    fun testWinner() {
        val game = requireNotNull(rootService.kaboo)
        val deck1 =
            mutableListOf(
                Card(CardSuit.SPADES, CardValue.KING),
                Card(CardSuit.CLUBS, CardValue.KING),
                Card(CardSuit.DIAMONDS, CardValue.KING),
                Card(CardSuit.HEARTS, CardValue.KING))
        game.player1.deck.addAll(deck1)

        val deck2 =
            mutableListOf(
                Card(CardSuit.SPADES, CardValue.TEN),
                Card(CardSuit.CLUBS, CardValue.TWO),
                Card(CardSuit.DIAMONDS, CardValue.THREE),
                Card(CardSuit.HEARTS, CardValue.EIGHT))
        game.player2.deck.addAll(deck2)

        assertEquals(game.player1.name, rootService.gameService.endGame())
    }

    @Test
    fun testDraw() {
        val game = requireNotNull(rootService.kaboo)
        val deck1 =
            mutableListOf(
                Card(CardSuit.SPADES, CardValue.THREE),
                Card(CardSuit.CLUBS, CardValue.EIGHT),
                Card(CardSuit.DIAMONDS, CardValue.TEN),
                Card(CardSuit.HEARTS, CardValue.TWO))
        game.player1.deck.addAll(deck1)

        val deck2 =
            mutableListOf(
                Card(CardSuit.SPADES, CardValue.TEN),
                Card(CardSuit.CLUBS, CardValue.TWO),
                Card(CardSuit.DIAMONDS, CardValue.THREE),
                Card(CardSuit.HEARTS, CardValue.EIGHT))
        game.player2.deck.addAll(deck2)

        assertEquals("It's a tie!", rootService.gameService.endGame())
    }
    */
}