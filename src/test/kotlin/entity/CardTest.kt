package entity

import kotlin.test.*

/**
 * Test cases for [Card] (toString - method)
 */
class CardTest {

    // Some cards to perform the tests with
    private val aceOfSpades = Card(CardSuit.SPADES, CardValue.ACE)
    private val jackOfClubs = Card(CardSuit.CLUBS, CardValue.JACK)
    private val queenOfHearts = Card(CardSuit.HEARTS, CardValue.QUEEN)
    private val jackOfDiamonds = Card(CardSuit.DIAMONDS, CardValue.JACK)

    // unicode characters for the suits, as those should be used by [WarCard.toString]
    private val heartsChar =  "♥" // ♥
    private val diamondsChar = "♦" // ♦
    private val spadesChar = "♠" // ♠
    private val clubsChar = "♣" // ♣

    /**
     * Check if to String produces the correct strings for some test cards
     * of all four suits.
     */
    @Test
    fun testToString() {
        assertEquals(spadesChar + "A", aceOfSpades.toString())
        assertEquals(clubsChar + "J", jackOfClubs.toString())
        assertEquals(heartsChar + "Q", queenOfHearts.toString())
        assertEquals(diamondsChar + "J", jackOfDiamonds.toString())
    }

    /**
     * Check if toString produces a 2 character string for every possible card
     * except the 10 (for which length=3 is ensured)
     */
    @Test
    fun testToStringLength() {
        CardSuit.entries.forEach { suit ->
            CardValue.entries.forEach { value ->
                if (value == CardValue.TEN)
                    assertEquals(3, Card(suit, value).toString().length)
                else
                    assertEquals(2, Card(suit, value).toString().length)
            }
        }
    }


}

