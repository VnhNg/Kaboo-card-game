package gui

import service.Refreshable
import service.RootService
import tools.aqua.bgw.components.layoutviews.GridPane
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.UIComponent
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.Visual

/**
 * The end game menu scene of the game.It shows the result, and you have the option to rematch.
 *
 * @constructor Creates a new Result Menu Scene with the specified rootService.
 */
class KabooEndGameMenuScene() : MenuScene
    (1920, 1080, background = Visual.EMPTY), Refreshable {
    // This pane is used to hold all components of the scene and easily center them on the screen
    private val contentPane = Pane<UIComponent>(
        width = 700,
        height = 600,
        posX = 1920 / 2 - 700 / 2,
        posY = 1080 / 2 - 600 / 2,
        visual = ColorVisual(java.awt.Color(0x0C2027))
    )

    // This label is used to display the title of the scene
    private val titleLabel = Label(
        text = "Winner: ",
        width = 700, height = 100,
        posX = 0, posY = 30,
        alignment = Alignment.CENTER,
        font = Font(100, java.awt.Color(0xFFFFFFF), "JetBrains Mono ExtraBold")
    )

    // This label is used to display the name of the winner
    private val winnerLabel = Label(
        text = "",
        width = 600, height = 200,
        posX = 50, posY = 150,
        alignment = Alignment.CENTER,
        font = Font(150, java.awt.Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(java.awt.Color(0x49585D))
    )

    // This button is used to come back to [NewGameMenuScene].
    val restartButton = Button(
        text = "New Game",
        width = 250, height = 100,
        posX = 50, posY = 450,
        font = Font(50, java.awt.Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(java.awt.Color(0x1CABDAFF))
    )

    // This button is used to come back to [NewGameMenuScene].
    val exitButton = Button(
        text = "Exit",
        width = 250, height = 100,
        posX = 400, posY = 450,
        font = Font(50, java.awt.Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(java.awt.Color(0x1C5182C))
    )

    private val deck1 = GridPane<Label> (
        posX = 1920/2-700, posY = 700,
        columns = 2, rows = 2,
        spacing = 30,
    )

    private val deck2 = GridPane<Label> (
        posX = 1920/2+700, posY = 700,
        columns = 2, rows = 2,
        spacing = 30
    )

    // Initialize the scene by setting the background color and adding all components to the content pane
    init {
        background = ColorVisual(java.awt.Color(12, 32, 39, 240))
        contentPane.addAll(titleLabel, winnerLabel, restartButton, exitButton)
        addComponents(contentPane, deck1, deck2)
    }

    /**
     * Refreshes the GUI after game has ended.
     *
     * @param winnerMassage The [String] is the message that will be shown in GUI at the end
     */
    override fun refreshAfterEndGame(winnerMassage: String) {
        winnerLabel.text = winnerMassage
    }

}

