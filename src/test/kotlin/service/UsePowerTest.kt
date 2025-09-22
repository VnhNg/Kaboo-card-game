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
 * Test usePower()
 */
class UsePowerTest {
    var rootService = RootService()
    var testRefreshMethodsCalled = TestRefreshMethodsCalled().apply {
        rootService.addRefreshables(this)
    }

    /**
     * Start the game as basis for further simulation
     */
    @BeforeTest
    fun setUp() {
        rootService.gameService.startGame("X", "Y")
        val game = rootService.kaboo
        assertNotNull(game)
    }

    /**
     * Test with valid and invalid cards
     */
    @Test
    fun testUsePower() {
        val game = rootService.kaboo
        assertNotNull(game)

        // invalid card
        game.currentPlayer.hand = Card(CardSuit.CLUBS, CardValue.FOUR)
        assertThrows<IllegalArgumentException> {rootService.playerService.usePower()}
        assertFalse { testRefreshMethodsCalled.refreshAfterUsePowerCalled }
        // valid card
        game.currentPlayer.hand = Card(CardSuit.CLUBS, CardValue.TEN)
        assertDoesNotThrow {rootService.playerService.usePower()}
        assertTrue { testRefreshMethodsCalled.refreshAfterUsePowerCalled }
        // hand card isn't thrown yet
        assert(game.usedStack.isEmpty())
    }

}