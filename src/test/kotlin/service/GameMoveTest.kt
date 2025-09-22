package service

import entity.Card
import entity.CardSuit
import entity.CardValue
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import tools.aqua.bgw.util.Stack
import kotlin.test.*

/**
 * Test gameMove()
 */
class GameMoveTest {
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
     * After gameMove() the currentPlayer has viewed 2 bottom cards.
     * viewedCard isn't set to true yet but after endTurn
     */
    @Test
    fun testViewedCards() {
        assertDoesNotThrow { rootService.gameService.gameMove() }
        val game = rootService.kaboo
        assertNotNull(game)
        assert(!game.currentPlayer.viewedCards)
        assertThrows<IllegalArgumentException> { rootService.playerService.drawCard(false) }
        assertTrue { testRefreshMethodsCalled.refreshAfterGameMoveCalled }
    }

    /**
     * Ensure gameMove() will call endGame() if the conditions are met
     */
    @Test
    fun testEndGameCalled() {
        val game = rootService.kaboo
        assertNotNull(game)
        game.newStack.popAll()
        assertDoesNotThrow { rootService.gameService.gameMove() }
        assertTrue { testRefreshMethodsCalled.refreshAfterEndGameCalled }
    }
}