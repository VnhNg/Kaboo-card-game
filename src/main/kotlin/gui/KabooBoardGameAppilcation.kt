package gui

import service.Refreshable
import tools.aqua.bgw.core.BoardGameApplication
import service.RootService

/**
 * Represents the main application for the SoPra board game.
 * The application initializes the [RootService] and displays the scenes.
 */
class KabooBoardGameAppilcation : BoardGameApplication("SoPra Game"), Refreshable {

    /**
     * The root service instance. This is used to call service methods and access the entity layer.
     */
    val rootService: RootService = RootService()

    /**
     * The main game scene displayed in the application.
     */
    private val gameScene = KabooBoardGameScene(rootService)

    /**
     * Start menu scene: add name of players and start game
     */
    private val startMenuScene = KabooStartMenuScene(rootService).apply {
        exitButton.onMouseClicked = { exit() }
    }

    /**
     * Next player scene
     */
    private  val nextPlayerMenuScene = KabooNextPlayerMenuScene(rootService)

    /**
     * End game scene
     */
    private val endGameMenuScene = KabooEndGameMenuScene().apply {
        restartButton.onMouseClicked = { this@KabooBoardGameAppilcation.showMenuScene(startMenuScene) }
        exitButton.onMouseClicked = { exit() }
    }

    /**
     * Initializes the application by displaying the scenes.
     */
    init {
        rootService.addRefreshables(
            this,
            gameScene,
            startMenuScene,
            nextPlayerMenuScene,
            endGameMenuScene
        )
        this.showGameScene(gameScene)
        this.showMenuScene(startMenuScene)

    }

    override fun refreshAfterGameStart() {
        hideMenuScene(500)
        showMenuScene(nextPlayerMenuScene)
    }

    override fun refreshAfterGameMove(canKnock: Boolean, canTakeUsedCard: Boolean) {
        hideMenuScene()
        showGameScene(gameScene)
    }

    override fun refreshAfterEachTurn() {
        showMenuScene(nextPlayerMenuScene)
    }

    override fun refreshAfterEndGame(winnerMassage: String) {
        hideMenuScene()
        showMenuScene(endGameMenuScene)
    }
}

