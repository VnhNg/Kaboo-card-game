package service

import entity.Card
import entity.CardSuit
import entity.CardValue
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * Test drawCard()
 */
class DrawCardTest {
    var rootService = RootService()
    var testRefreshMethodsCalled = TestRefreshMethodsCalled().apply {
        rootService.addRefreshables(this)
    }

    /**
     * Simply start the game
     */
    @BeforeTest
    fun setUp() {
        rootService.gameService.startGame("X", "Y")
        val game = rootService.kaboo
        assertNotNull(game)
    }

    /**
     * Valid draW: Card was removed from drawn stack and added to hand
     */
    @Test
    fun testValidDraw() {
        val game = rootService.kaboo
        assertNotNull(game)
        game.currentPlayer.viewedCards = true
        val card = game.newStack.peek()
        rootService.playerService.drawCard(false)
        assertEquals(card, game.currentPlayer.hand)
        assertNotEquals(card, game.newStack.peek())
        assertTrue { testRefreshMethodsCalled.refreshAfterDrawCalled }
    }

    @Test
    fun testDrawUsedStack() {
        val game = rootService.kaboo
        assertNotNull(game)
        game.currentPlayer.viewedCards = true
        assertThrows<IllegalArgumentException> { rootService.playerService.drawCard(true) }
        val card = Card(CardSuit.CLUBS, CardValue.TEN)
        game.usedStack.push(card)
        rootService.playerService.drawCard(true)
        assertEquals(card, game.currentPlayer.hand)
        assertTrue { testRefreshMethodsCalled.refreshAfterDrawCalled }
    }

    /**
     * Draw from empty stack
     */
    @Test
    fun testDrawEmpty() {
        assertThrows<IllegalArgumentException> { rootService.playerService.drawCard(true) }

        val game = rootService.kaboo
        assertNotNull(game)
        game.newStack.popAll()
        assertThrows<IllegalArgumentException> { rootService.playerService.drawCard(false) }
    }
}