package gui

import service.Refreshable
import service.RootService
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.UIComponent
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color

/**
 * Next player scene for Hotseat-Mode
 *
 * @param rootService The root service to which this scene belongs
 */
class KabooNextPlayerMenuScene(val rootService: RootService) : MenuScene(1920, 1080), Refreshable {

    // This pane is used to hold all components of the scene and easily center them on the screen
    private val contentPane = Pane<UIComponent>(
        width = 700,
        height = 900,
        posX = 1920 / 2 - 700 / 2,
        posY = 1080 / 2 - 900 / 2,
        visual = ColorVisual.DARK_GRAY
    )

    // This label is used to display the title of the scene
    private val titleLabel = Label(
        text = "",
        width = 700,
        height = 200,
        posX = 0,
        posY = 30,
        alignment = Alignment.CENTER,
        font = Font(100, Color(0xFFFFFFF), "JetBrains Mono ExtraBold")
    )

    private val playerLabel = Label(
        text = "",
        width = 600,
        height = 300,
        posX = 50,
        posY = 300,
        alignment = Alignment.CENTER,
        font = Font(150, java.awt.Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(java.awt.Color(0x5C4141))
    )

    // This button is used to confirm next player
    private val nextTurnButton = Button(
        text = "",
        width = 280, height = 75,
        posX = 210, posY = 700,
    ).apply {
        onMouseClicked = { rootService.gameService.gameMove() }
    }

    // Initialize the scene by setting the background color and adding all components to the content pane
    init {
        background = ColorVisual(Color(12, 32, 39, 240))
        contentPane.addAll(titleLabel, playerLabel, nextTurnButton)
        addComponents(contentPane)
    }

    /** Refreshes the GUI after a new game has been started. */
    override fun refreshAfterGameStart() {
        val game = requireNotNull(rootService.kaboo)
        titleLabel.text ="First player"
        playerLabel.text =  game.currentPlayer.name
        nextTurnButton.apply {
            text = "START!"
            font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold")
            visual = ColorVisual(java.awt.Color(0x1CABDAFF))
        }
    }

    /** Refreshes the GUI after a turn has ended. */
    override fun refreshAfterEachTurn() {
        val game = requireNotNull(rootService.kaboo)
        titleLabel.text ="Next player"
        playerLabel.text =  game.currentPlayer.name
        nextTurnButton.apply {
            text = "Confirm"
            font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold")
            visual = ColorVisual(Color(0x1C5182C))
        }
    }
}

