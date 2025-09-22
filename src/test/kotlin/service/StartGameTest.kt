package service

import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * Test startGame()
 */
class StartGameTest {
    var rootService = RootService()
    var testRefreshMethodsCalled = TestRefreshMethodsCalled().apply {
        rootService.addRefreshables(this)
    }

    /**
     * Start a game
     */
    @BeforeTest
    fun setUp() {
        rootService.gameService.startGame("Player1", "Player2")
        assertNotNull(rootService.kaboo)
        assertTrue { testRefreshMethodsCalled.refreshAfterGameStartCalled }
        testRefreshMethodsCalled.reset()
    }

    /**
     * start game when another one is running.
     */
    @Test
    fun testStartSecondGame () {
        assertThrows<IllegalArgumentException> {
            rootService.gameService.startGame("Player1", "Player2")
        }
        assertFalse { testRefreshMethodsCalled.refreshAfterGameStartCalled }
    }

    /**
     * config same name for both players
     */
    @Test
    fun testSameName () {
        rootService.kaboo = null
        assertThrows<IllegalArgumentException> {
            rootService.gameService.startGame("Player1", "Player1")
        }
        assertFalse { testRefreshMethodsCalled.refreshAfterGameStartCalled }
    }

    /**
     * test number card in newStack, usedStack and decks of 2 players
     */
    @Test
    fun testCardNumbers () {
        val game = requireNotNull(rootService.kaboo)
        assertEquals(52 - 8, game.newStack.size)
        assertEquals(0, game.usedStack.size)
        assertEquals(4, game.player1.deck.size)
        assertEquals(4, game.player2.deck.size)
    }

    /**
     * ensure noone has viewed 2 under cards or knocked
     */
    @Test
    fun testPlayerStatus () {
        val game = requireNotNull(rootService.kaboo)
        assert(!game.player1.viewedCards)
        assert(!game.player1.knocked)
        assert(!game.player2.viewedCards)
        assert(!game.player2.knocked)
    }


}