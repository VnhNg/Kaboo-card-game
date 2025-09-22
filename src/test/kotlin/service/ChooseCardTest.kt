package service

import entity.Card
import entity.CardSuit
import entity.CardValue
import entity.DeckPosition
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * Test chooseCard()
 */
class ChooseCardTest {
    var rootService = RootService()
    var testRefreshMethodsCalled = TestRefreshMethodsCalled().apply {
        rootService.addRefreshables(this)
    }

    /**
     * Start the game and initialize one card for each player for swap
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

    /**
     * Invalid Cases
     */
    @Test
    fun testInvalidSwapOther() {
        val game = rootService.kaboo
        assertNotNull(game)

        // No card in hand
        assertThrows<IllegalArgumentException> {
            rootService.playerService.chooseCard(DeckPosition.BOTTOM_LEFT, game.player2)
        }

        // Wrong card
        game.player1.hand = Card(CardSuit.HEARTS, CardValue.SEVEN)
        assertThrows<IllegalArgumentException> {
            rootService.playerService.chooseCard(DeckPosition.BOTTOM_LEFT, game.player2)
        }

        // Choose 2 cards of same player
        game.player1.hand = Card(CardSuit.HEARTS, CardValue.JACK)
        game.player1.ownSelected = DeckPosition.TOP_LEFT
        assertThrows<IllegalArgumentException> {
            rootService.playerService.chooseCard(DeckPosition.BOTTOM_LEFT, game.player1)
        }
    }

    /**
     * Swap with Jack card
     */
    @Test
    fun testJackSwapOther() {
        val game = rootService.kaboo
        assertNotNull(game)
        val nineOfSpades = Card(CardSuit.SPADES, CardValue.NINE)
        val kingOfClubs = Card(CardSuit.CLUBS, CardValue.KING)
        game.player1.hand = Card(CardSuit.HEARTS, CardValue.JACK)
        assertDoesNotThrow {
            rootService.playerService.chooseCard(DeckPosition.TOP_LEFT, game.player1)
        }
        game.player1.ownSelected = DeckPosition.TOP_LEFT


        assertThrows<IllegalArgumentException> {
            rootService.playerService.chooseCard(DeckPosition.BOTTOM_LEFT, game.player1)
        }
        rootService.playerService.chooseCard(DeckPosition.BOTTOM_RIGHT, game.player2)
        assertEquals(kingOfClubs ,game.player1.deck[0])
        assertEquals(nineOfSpades ,game.player2.deck[3])
        assertEquals(Card(CardSuit.HEARTS, CardValue.JACK) ,game.usedStack.peek())

        assertTrue { testRefreshMethodsCalled.refreshAfterSwapOtherCalled }
        assertFalse { testRefreshMethodsCalled.refreshAfterChooseCardCalled }
    }

    /**
     * Swap with Queen card
     */
    @Test
    fun testQueenSwapOther() {
        val game = rootService.kaboo
        assertNotNull(game)
        val nineOfSpades = Card(CardSuit.SPADES, CardValue.NINE)
        val kingOfClubs = Card(CardSuit.CLUBS, CardValue.KING)
        game.player1.ownSelected = DeckPosition.TOP_LEFT
        game.player1.hand = Card(CardSuit.DIAMONDS, CardValue.QUEEN)

        assertThrows<IllegalArgumentException> {
            rootService.playerService.chooseCard(DeckPosition.BOTTOM_LEFT, game.player1)
        }
        rootService.playerService.chooseCard(DeckPosition.BOTTOM_RIGHT, game.player2)
        assertEquals(nineOfSpades ,game.player1.deck[0])
        assertEquals(kingOfClubs ,game.player2.deck[3])
        assert(game.usedStack.isEmpty())
        assertFalse { testRefreshMethodsCalled.refreshAfterSwapOtherCalled }
        assertTrue { testRefreshMethodsCalled.refreshAfterChooseCardCalled }
    }

    /**
     * First card for swapping is chosen
     */
    @Test
    fun testFirstChoice() {
        val game = rootService.kaboo
        assertNotNull(game)
        val nineOfSpades = Card(CardSuit.SPADES, CardValue.NINE)
        val kingOfClubs = Card(CardSuit.CLUBS, CardValue.KING)
        game.player1.hand = Card(CardSuit.DIAMONDS, CardValue.QUEEN)

        assertDoesNotThrow {
            rootService.playerService.chooseCard(DeckPosition.BOTTOM_LEFT, game.player1)
        }

        assertEquals(nineOfSpades ,game.player1.deck[0])
        assertEquals(kingOfClubs ,game.player2.deck[3])
        assertFalse { testRefreshMethodsCalled.refreshAfterSwapOtherCalled }
        assertFalse { testRefreshMethodsCalled.refreshAfterChooseCardCalled }
    }
}

