package entity

/**
 * Enum to represent between the four card positions of each player, also coming with an index
 */
enum class DeckPosition {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    ;

    /**
     * provide an Int typ to access the corresponding position in deck of [Player].
     * Returns one of: 0/1/2/3
     */
    fun toInt(): Int = when(this) {
        TOP_LEFT -> 0
        TOP_RIGHT-> 1
        BOTTOM_LEFT -> 2
        BOTTOM_RIGHT -> 3
    }
}

