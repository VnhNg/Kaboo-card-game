package service


import entity.DeckPosition
import kotlin.test.*

/**
 * Test endTurn()
 */
class EndTurnTest {
    var rootService = RootService()
    var testRefreshMethodsCalled = TestRefreshMethodsCalled().apply {
        rootService.addRefreshables(this)
    }

    /**
     * Start the game
     * Assign player1 as currentPlayer, that should be changed to player2 after end turn
     * Also config some properties of player1, those should be reset after end turn
     */
    @BeforeTest
    fun setUp() {
        rootService.gameService.startGame("X", "Y")
        val game = rootService.kaboo
        assertNotNull(game)
        game.player1.ownSelected = DeckPosition.TOP_LEFT
        game.player1.otherSelected = DeckPosition.BOTTOM_RIGHT
        game.currentPlayer = game.player1
    }

    /**
     * ensure the currentPlayer was changed and all selected cards were reset
     */
    @Test
    fun testEndTurn() {
        rootService.gameService.endTurn()
        val game = rootService.kaboo
        assertNotNull(game)
        assertEquals(game.player2, game.currentPlayer)
        assertNull(game.player1.ownSelected)
        assertNull(game.player1.otherSelected)
        assertNull(game.player2.ownSelected)
        assertNull(game.player2.otherSelected)
        assertTrue { testRefreshMethodsCalled.refreshAfterEachTurnCalled }
    }
}