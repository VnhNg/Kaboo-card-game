package gui

import entity.*
import service.Refreshable
import service.RootService
import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.layoutviews.GridPane
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.UIComponent
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.event.MouseButtonType
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color

/**
 * The GameScene class is a BoardGameScene that displays the game board and all game components.
 *
 * @property rootService The associated [RootService]
 */
class KabooBoardGameScene(private val rootService: RootService): BoardGameScene(), Refreshable {

    // Card Image
    private val cardImageLoader = CardImageLoader()

    // BidirectionalMap is used to map Card objects to CardView objects
    private val cardMap = BidirectionalMap<Card, CardView>(
    ).apply {
        CardValue.entries.forEach { value ->
            CardSuit.entries.forEach { suit ->
                this[Card(suit, value)] = CardView(
                    posX = 0, posY = 0,
                    width = 162, height = 250,
                    front = cardImageLoader.frontImageFor(suit, value),
                    back = cardImageLoader.backImage
                )
            }
        }
    }

    private val hand1 = GridPane<CardView> (
        posX = 1920/2-700, posY = 175,
        columns = 1, rows = 1
    )

    private val hand2 = GridPane<CardView> (
        posX = 1920/2+700, posY = 175,
        columns = 1, rows = 1
    )

    private var handCardView = CardView(
        width = 162, height = 250,
        front = cardImageLoader.blankImage,
        back = cardImageLoader.blankImage,
    )

    private val deck1 = GridPane<CardView> (
        posX = 1920/2-700, posY = 700,
        columns = 2, rows = 2,
        spacing = 30,
    ).apply {
        opacity = 0.5
    }

    private val deck2 = GridPane<CardView> (
        posX = 1920/2+700, posY = 700,
        columns = 2, rows = 2,
        spacing = 30
    ).apply {
        opacity = 0.5
    }

    private val name1 = Label(
        posX = 1920/2-200/2-700, posY = 1000,
        width = 200, height = 50,
        text = "", font = Font(fontWeight = Font.FontWeight.EXTRA_BOLD),
        visual = ColorVisual.RED
    )

    private val name2 = Label(
        posX = 1920/2-200/2+700, posY =1000,
        width = 200, height = 50,
        text = "", font = Font(fontWeight = Font.FontWeight.EXTRA_BOLD),
        visual = ColorVisual.BLUE
    )

    private val usedStack = CardStack<CardView>(
        posX = 1920/2-162/2-200, posY = 100,
        width = 162, height = 250,
        visual = ColorVisual.WHITE
    ).apply {
        dropAcceptor = { dragEvent -> dragEvent.draggedComponent is CardView }
        onDragDropped = {rootService.playerService.disCard()}
        //TODO("show used cards")
    }

    private val newStack = CardStack<CardView>(
        posX = 1920/2-162/2+200, posY = 100,
        width = 162, height = 250,
        visual = ColorVisual.WHITE
    )

    private val knockButton = Button(
        posX = 1920/2-100/2, posY =1080/2-100,
        width = 100, height = 60,
        text = "Knock!", font = Font(fontWeight = Font.FontWeight.EXTRA_BOLD),
        visual = ColorVisual.RED
    ).apply {
        onMouseClicked = {rootService.playerService.knock()}
    }

    private val nextTurnButton = Button(
        posX = 1920/2-100/2, posY =1080/2-100,
        width = 100, height = 60,
        text = "Next!", font = Font(fontWeight = Font.FontWeight.EXTRA_BOLD),
        visual = ColorVisual.GREEN
    ).apply {
        onMouseClicked = {rootService.gameService.endTurn()}
    }

    private val titleLabel = Label(
        text = "SWAP?",
        width = 500,
        height = 75,
        posX = 0,
        posY = 0,
        alignment = Alignment.CENTER,
        font = Font(75, Color(0x285695), "JetBrains Mono ExtraBold")
    )
    // Swap cards
    private val yesButton = Button(
        text = "Yes",
        width = 150, height = 100,
        posX = 50, posY = 150,
        font = Font(50, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x1CABDAFF))
    )
    // End turn without swapping
    private val noButton = Button(
        text = "No",
        width = 150, height = 100,
        posX = 300, posY = 150,
        font = Font(50, Color(0xFFFFFFF), "JetBrains Mono ExtraBold "),
        visual = ColorVisual(Color(0x1C5182C))
    )

    private val swapPane = Pane<UIComponent>(
        width = 500,
        height = 300,
        posX = 1920/2 - 500/2,
        posY = 1080/2 - 300/2,
        visual = ColorVisual(Color(0xC1BA89))
    ).apply { addAll(titleLabel, yesButton, noButton) }


    init {
        background = ColorVisual(Color(12, 32, 39, 240))
        addComponents(
            hand1, hand2,
            deck1, deck2,
            name1, name2,
            newStack, usedStack,
            knockButton
        )
    }

    // Convert Grid indices into deck position
    private fun getDeckPosition(rowIndex: Int, columnIndex: Int): DeckPosition {
        return when {
            rowIndex == 0 && columnIndex == 0 -> DeckPosition.TOP_LEFT
            rowIndex == 0 && columnIndex == 1 -> DeckPosition.TOP_RIGHT
            rowIndex == 1 && columnIndex == 0 -> DeckPosition.BOTTOM_LEFT
            rowIndex == 1 && columnIndex == 1 -> DeckPosition.BOTTOM_RIGHT
            else -> throw IllegalArgumentException("Unexpected grid position")
        }
    }

    // Clear all Cards
    private  fun clearBoard() {
        hand1.set(0, 0, null)
        hand2.set(0, 0, null)

        deck1.forEach {
            element ->
            deck1.set(columnIndex = element.columnIndex, rowIndex = element.rowIndex, component = null)
        }
        deck2.forEach {
            element ->
            deck2.set(columnIndex = element.columnIndex, rowIndex = element.rowIndex, component = null)
        }

        newStack.clear()
        usedStack.clear()
    }

    private fun creatNewCardMap() {
        CardValue.entries.forEach { value ->
            CardSuit.entries.forEach { suit ->
                cardMap[Card(suit, value)] = CardView(
                    posX = 0, posY = 0,
                    width = 162, height = 250,
                    front = cardImageLoader.frontImageFor(suit, value),
                    back = cardImageLoader.backImage
                )
            }
        }

    }

    /**
     * Set up the Startboard (hands, decks and new stack)
     */
    override fun refreshAfterGameStart() {
        clearBoard()
        creatNewCardMap()
        val game = requireNotNull(rootService.kaboo)
        val deckOfPlayer1 = game.player1.deck
        val deckOfPlayer2 = game.player2.deck

        hand1.set(0, 0,
            CardView(
                width = 162, height = 250,
                front = cardImageLoader.blankImage,
                back = cardImageLoader.blankImage,
                )
        )
        hand2.set(0, 0,
            CardView(
                width = 162, height = 250,
                front = cardImageLoader.blankImage,
                back = cardImageLoader.blankImage,
                )
        )

        deck1.set(0, 0, cardMap.forward(deckOfPlayer1[0]))
        deck1.set(1, 0, cardMap.forward(deckOfPlayer1[1]))
        deck1.set(0, 1, cardMap.forward(deckOfPlayer1[2]))
        deck1.set(1, 1, cardMap.forward(deckOfPlayer1[3]))

        deck2.set(0, 0, cardMap.forward(deckOfPlayer2[0]))
        deck2.set(1, 0, cardMap.forward(deckOfPlayer2[1]))
        deck2.set(0, 1, cardMap.forward(deckOfPlayer2[2]))
        deck2.set(1, 1, cardMap.forward(deckOfPlayer2[3]))

        name1.text = game.player1.name
        name2.text = game.player2.name

        game.newStack.peekAll().reversed().forEach {card -> newStack.push(cardMap.forward(card))}
    }

    /**
     * Synchron the opacity of both decks
     * Hide or show the knock button and nextTurn button
     * Disable or enable the components
     */
    override fun refreshAfterGameMove(canKnock: Boolean, canTakeUsedCard: Boolean) {
        // Reset opacity of both decks
        deck1.apply {
            opacity = 0.5
            for (element in this) {
                val cardView = requireNotNull(deck1.get(element.columnIndex, element.rowIndex))
                cardView.opacity = 1.0
            }
        }
        deck2.apply {
            opacity = 0.5
            for (element in this) {
                val cardView = requireNotNull(deck2.get(element.columnIndex, element.rowIndex))
                cardView.opacity = 1.0
            }
        }
        val game = requireNotNull(rootService.kaboo)
        val currentPlayer = game.currentPlayer

        if (!currentPlayer.viewedCards) {
            // first round
            usedStack.onMouseClicked = null
            newStack.onMouseClicked = null
            removeComponents(knockButton)
            addComponents(nextTurnButton)
        } else {
            // normal round
            when {
                canKnock -> {addComponents(knockButton)}
                else -> {removeComponents(knockButton)}
            }
            if (canTakeUsedCard)
                usedStack.onMouseClicked = {rootService.playerService.drawCard(true)}
            newStack.onMouseClicked = {rootService.playerService.drawCard(false)}
        }
    }

    private fun interactiveHandCard (cardView: CardView, usablePower: Boolean) {
        cardView.onMouseEntered = {
            cardView.scale(1.1)
        }
        cardView.onMouseExited = {
            cardView.scale(1)
        }
        cardView.isDraggable = true
        if (usablePower)
            cardView.onMouseClicked = { mouseEvent ->
                if (mouseEvent.button == MouseButtonType.RIGHT_BUTTON)
                    rootService.playerService.usePower()
            }
    }

    private fun nonInteractiveHandCard (cardView: CardView) {
        cardView.scale(1)
        cardView.onMouseEntered = null
        cardView.onMouseExited = null
        cardView.isDraggable = false
        cardView.onMouseClicked = null
    }

    /**
     * Check the drawn card and highlight the interactable components:
     * usedStack (discard)
     * ownDeck (swapSelf)
     * usePower - if applicable
     *
     * Postconditions:
     * The card is now in player's hand card position, draggable
     * right click on hand card to usePower
     * ownDeck is enabled to accept cardView drop (swapSelf)
     * adjust usedStack.onDragDropped after discardable
     * "Knock" button is removed
     */
    override fun refreshAfterDraw(discardable: Boolean, usablePower: Boolean) {
        val game = requireNotNull(rootService.kaboo)
        val currentPlayer = game.currentPlayer
        val currentHand = if (currentPlayer == game.player1) hand1 else hand2
        val drawnStack = if (discardable) newStack else usedStack

        // Hand card: shown face up in right place, interactable as a hand card
        handCardView = drawnStack.pop()
        addComponents(
            handCardView.apply {
                posX = currentHand.posX-162/2; posY = currentHand.posY-250/2
                interactiveHandCard(this, usablePower)
                showFront()
            }
        )
        // new stack: Can't draw
        newStack.onMouseClicked = null
        // used stack: Can't draw, ready to discard if hand card is discardable
        usedStack.apply {
            onMouseClicked = null
            onDragDropped = if (discardable) { _ -> rootService.playerService.disCard()} else null
        }
        // Own deck: ready to swapSelf
        val ownDeck = if (currentPlayer == game.player1) deck1 else deck2
        ownDeck.apply {
            opacity = 1.0
            forEach {
                val deckCard = requireNotNull(ownDeck.get(it.columnIndex, it.rowIndex))
                val deckPosition = getDeckPosition(rowIndex = it.rowIndex, columnIndex =  it.columnIndex)
                deckCard.dropAcceptor = { dragEvent -> dragEvent.draggedComponent is CardView }
                deckCard.onDragDropped = { rootService.playerService.swapSelf(deckPosition) }
            }
        }
        // Other deck: Can't swapSelf
        val otherDeck = if (currentPlayer == game.player2) deck1 else deck2
        otherDeck.apply {
            opacity = 0.5
            forEach { element ->
                val deckCard = requireNotNull(otherDeck.get(element.columnIndex, element.rowIndex))
                deckCard.onDragDropped = null
            }
        }
        // Knock button
        removeComponents(knockButton)
    }

    /**
     * Deactivate handCardView
     * Push it to usedStack
     */
    override fun refreshAfterDiscard() {
        nonInteractiveHandCard(handCardView)
        usedStack.push(handCardView)
        removeComponents(handCardView)
    }

    private fun getGridIndices(position: DeckPosition): Pair<Int, Int> {
        return when (position) {
            DeckPosition.TOP_LEFT -> 0 to 0
            DeckPosition.TOP_RIGHT -> 0 to 1
            DeckPosition.BOTTOM_LEFT -> 1 to 0
            DeckPosition.BOTTOM_RIGHT -> 1 to 1
        }
    }

    private fun interactiveDeckCard (cardView: CardView, position: DeckPosition) {
        cardView.dropAcceptor = { dragEvent -> dragEvent.draggedComponent is CardView }
        cardView.onDragDropped = {rootService.playerService.swapSelf(position)}
    }

    private fun nonInteractiveDeckCard (cardView: CardView) {
        cardView.dropAcceptor = null
        cardView.onDragDropped = null
    }

    /**
     * Turn hand card to a deck card and replace the chosen deck card with it
     * Deactivate the chosen deck card and push it to the used stack
     */
    override fun refreshAfterSwapSelf(position: DeckPosition) {
        val rowIndex = getGridIndices(position).first
        val columnIndex = getGridIndices(position).second
        val game = requireNotNull(rootService.kaboo)
        val currentPlayer = game.currentPlayer
        val ownDeck = if (currentPlayer == game.player1) deck1 else deck2

        val deckCard = requireNotNull(ownDeck.get(columnIndex = columnIndex, rowIndex = rowIndex))
        deckCard.apply {
            nonInteractiveDeckCard(this)
            showFront()
            usedStack.push(this)
        }

        handCardView.apply {
            nonInteractiveHandCard(this)
            showBack()
            interactiveDeckCard(this, position)
        }
        val cardBackEnd = cardMap.forward(game.currentPlayer.deck[position.toInt()])

        ownDeck.set(columnIndex = columnIndex, rowIndex = rowIndex, component =  cardBackEnd)
        removeComponents(handCardView)
    }

    private fun shutDownDeckOnMouseClicked(deck: GridPane<CardView>) {
        deck.forEach { element ->
            val cardView = requireNotNull(deck.get(element.columnIndex, element.rowIndex))
            cardView.apply {
                opacity = 0.5
                onMouseEntered = null
                onMouseExited = null
                onMouseClicked = null
            }
        }
    }

    private fun deckOnMouseClicked(deck: GridPane<CardView>) {
        val game = requireNotNull(rootService.kaboo)
        val hand = requireNotNull(game.currentPlayer.hand)
        val opponent = if (game.currentPlayer == game.player1) game.player2 else game.player1

        for (element in deck) {
            val cardView = requireNotNull(deck.get(element.columnIndex, element.rowIndex))
            val deckPosition = getDeckPosition(rowIndex = element.rowIndex, columnIndex = element.columnIndex)
            cardView.apply {
                onMouseEntered = {scale(1.1)}
                onMouseExited = {scale(1)}
                if (hand.value == CardValue.SEVEN || hand.value == CardValue.EIGHT) {
                    onMouseClicked = {
                        rootService.playerService.peekCardPlayer(deckPosition, game.currentPlayer)
                        shutDownDeckOnMouseClicked(deck)
                        opacity = 1.0
                    }
                } else if (hand.value == CardValue.NINE || hand.value == CardValue.TEN) {
                    onMouseClicked = {
                        rootService.playerService.peekCardPlayer(deckPosition, opponent)
                        shutDownDeckOnMouseClicked(deck)
                        opacity = 1.0
                    }
                } else if (hand.value == CardValue.JACK || hand.value == CardValue.QUEEN) {
                    when (deck) {
                        deck1 -> onMouseClicked = {
                            rootService.playerService.chooseCard(deckPosition, game.player1)
                            shutDownDeckOnMouseClicked(deck)
                            opacity = 1.0
                        }
                        else -> onMouseClicked = {
                            rootService.playerService.chooseCard(deckPosition, game.player2)
                            shutDownDeckOnMouseClicked(deck)
                            opacity = 1.0
                        }
                    }
                }
            }
        }
    }
    /**
     * Highlight and activate the decks depending on the used power of the hand card
     * Deactivate hand card
     */
    override fun refreshAfterUsePower(highlightDeckPlayer: Boolean, highlightDeckOpponent: Boolean) {
        val game = requireNotNull(rootService.kaboo)
        // Reset the opacity of both decks before highlighting, adjust opacity for single deck card
        deck1.opacity = 1.0
        deck2.opacity = 1.0
        shutDownDeckOnMouseClicked(deck1)
        shutDownDeckOnMouseClicked(deck2)
        // Hand card: deactivated
        nonInteractiveHandCard(handCardView)
        // DeckPlayer: Highlighted and activated depending on the used power of the hand card
        val deckPlayer = if (game.currentPlayer == game.player1) deck1 else deck2
        if (highlightDeckPlayer) {
            deckOnMouseClicked(deckPlayer)
            deckPlayer.forEach { element ->
                val cardView = requireNotNull(deckPlayer.get(element.columnIndex, element.rowIndex))
                cardView.opacity = 1.0
            }
        }
        // DeckOpponent: Highlighted and activated depending on the used power of the hand card
        val deckOpponent = if (game.currentPlayer == game.player2) deck1 else deck2
        if (highlightDeckOpponent) {
            deckOnMouseClicked(deckOpponent)
            deckOpponent.forEach { element ->
                val cardView = requireNotNull(deckOpponent.get(element.columnIndex, element.rowIndex))
                cardView.opacity = 1.0
            }
        }
        // Jack card
        if (!highlightDeckPlayer && !highlightDeckOpponent) {
            deckOnMouseClicked(deckPlayer)
            deckPlayer.forEach { element ->
                val cardView = requireNotNull(deckPlayer.get(element.columnIndex, element.rowIndex))
                cardView.opacity = 0.5
            }
            deckOnMouseClicked(deckOpponent)
            deckOpponent.forEach { element ->
                val cardView = requireNotNull(deckOpponent.get(element.columnIndex, element.rowIndex))
                cardView.opacity = 0.5
            }
        }
    }

    /**
     * Reveal chosen deck card
     * Push hand card to used stack
     * Add next turn button for passing to the turn
     */
    override fun refreshAfterPeekCardPlayer(positionToPeak: DeckPosition, playerToPeak: Player) {
        val game = requireNotNull(rootService.kaboo)
        val highlightedDeck = if (playerToPeak == game.player1) deck1 else deck2
        val rowIndex = getGridIndices(positionToPeak).first
        val columnIndex = getGridIndices(positionToPeak).second
        // Chosen card: flipped and rescaled
        requireNotNull(highlightedDeck.get(columnIndex = columnIndex, rowIndex = rowIndex)).apply {
            showFront()
            scale(1)
        }
        // Hand card: pushed to used stack
        if (game.currentPlayer.viewedCards) {
            usedStack.push(handCardView)
            removeComponents(handCardView)
        }
        // Next button: added
        if (game.currentPlayer.viewedCards) addComponents(nextTurnButton)
    }

    /**
     * Reveals one card in each deck
     * Display a window with 2 options: swap or not for the player to decide
     */
    override fun refreshAfterChooseCard() {
        val game = requireNotNull(rootService.kaboo)
        // Reveal 1 card in own deck
        val ownIndices = getGridIndices(requireNotNull(game.currentPlayer.ownSelected))
        val ownDeck = if (game.currentPlayer == game.player1) deck1 else deck2
        requireNotNull(ownDeck.get(rowIndex = ownIndices.first, columnIndex = ownIndices.second)).showFront()
        // Reveal 1 card in other deck
        val otherIndices = getGridIndices(requireNotNull(game.currentPlayer.otherSelected))
        val otherDeck = if (game.currentPlayer == game.player2) deck1 else deck2
        requireNotNull(otherDeck.get(rowIndex = otherIndices.first, columnIndex = otherIndices.second)).showFront()
        // Display swap window
        yesButton.onMouseClicked = {
            val ownPosition = requireNotNull(game.currentPlayer.ownSelected)
            val otherPosition = requireNotNull(game.currentPlayer.otherSelected)
            rootService.playerService.swapOther(ownPosition = ownPosition, otherPosition = otherPosition)
        }
        noButton.onMouseClicked = {
            usedStack.push(handCardView)
            removeComponents(handCardView)
            rootService.gameService.endTurn()
        }
        addComponents(swapPane)
    }

    /**
     * Update card views in both decks
     * Push hand card to used stack after using its power
     */
    override fun refreshAfterSwapOther(ownPosition: DeckPosition, otherPosition: DeckPosition) {
        val game = requireNotNull(rootService.kaboo)
        val currentPlayer = game.currentPlayer
        val opponent = if (currentPlayer == game.player1) game.player2 else game.player1
        val ownDeck = if (currentPlayer == game.player1) deck1 else deck2
        val otherDeck = if (currentPlayer == game.player2) deck1 else deck2

        // Update card views in both decks
        ownDeck.set(
            rowIndex = getGridIndices(ownPosition).first,
            columnIndex = getGridIndices(ownPosition).second,
            component = cardMap.forward(game.currentPlayer.deck[ownPosition.toInt()])
        )
        otherDeck.set(
            rowIndex = getGridIndices(otherPosition).first,
            columnIndex = getGridIndices(otherPosition).second,
            component = cardMap.forward(opponent.deck[otherPosition.toInt()])
        )
        // Hand card: pushed to used stack
        usedStack.push(handCardView)
        removeComponents(handCardView)
    }


    override fun refreshAfterEndGame(winnerMassage: String) {
        deck1.apply {
            for (element in this) {
                val cardView = requireNotNull(deck1.get(element.columnIndex, element.rowIndex))
                cardView.apply {
                    scale(1)
                    showFront()
                    opacity = 1.0
                    onMouseEntered = null
                    onMouseExited = null
                    onMouseClicked = null
                    onDragDropped = null
                }
            }
        }
        deck2.apply {
            for (element in this) {
                val cardView = requireNotNull(deck2.get(element.columnIndex, element.rowIndex))
                cardView.apply {
                    scale(1)
                    showFront()
                    opacity = 1.0
                    onMouseEntered = null
                    onMouseExited = null
                    onMouseClicked = null
                    onDragDropped = null
                }
            }
        }
    }

    /**
     * Flip down and disable all cards in both decks
     * Remove all buttons and panes
     */
    override fun refreshAfterEachTurn() {
        // Flip down and disable all cards in both decks
        deck1.apply {
            opacity = 0.5
            for (element in this) {
                val cardView = requireNotNull(deck1.get(element.columnIndex, element.rowIndex))
                cardView.apply {
                    scale(1)
                    showBack()
                    opacity = 1.0
                    onMouseEntered = null
                    onMouseExited = null
                    onMouseClicked = null
                    onDragDropped = null
                }
            }
        }
        deck2.apply {
            opacity = 0.5
            for (element in this) {
                val cardView = requireNotNull(deck2.get(element.columnIndex, element.rowIndex))
                cardView.apply {
                    scale(1)
                    showBack()
                    opacity = 1.0
                    onMouseEntered = null
                    onMouseExited = null
                    onMouseClicked = null
                    onDragDropped = null
                }
            }
        }
        // Remove all buttons and panes
        removeComponents(knockButton, nextTurnButton, swapPane)
    }
}

