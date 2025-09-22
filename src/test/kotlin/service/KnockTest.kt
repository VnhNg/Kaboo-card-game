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
 * Test knock()
 */
class KnockTest {
    var rootService = RootService()
    var testRefreshMethodsCalled = TestRefreshMethodsCalled().apply {
        rootService.addRefreshables(this)
    }

    /**
     * Start the game where both players haven't knocked yet and assign player1 as currentPlayer
     */
    @BeforeTest
    fun setUp() {
        rootService.gameService.startGame("X", "Y")
        val game = rootService.kaboo
        assertNotNull(game)
        game.currentPlayer = game.player1
        assert(!game.player1.knocked)
        assert(!game.player2.knocked)
    }

    /**
     * Opponent hasn't knocked
     */
    @Test
    fun testValidKnock() {
        val game = rootService.kaboo
        assertNotNull(game)
        game.currentPlayer.viewedCards = true
        rootService.playerService.knock()
        assert(game.player1.knocked)
        assert(!game.player2.knocked)
        assertTrue { testRefreshMethodsCalled.refreshAfterEachTurnCalled }
    }


    /**
     * Opponent has knocked
     */
    @Test
    fun testInvalidKnock() {
        val game = rootService.kaboo
        assertNotNull(game)
        game.player2.knocked = true
        assertThrows<IllegalArgumentException> {rootService.playerService.knock()}
    }
}