package gui

import service.Refreshable
import service.RootService
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.components.uicomponents.UIComponent
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color

/**
 * The main menu scene of the game.
 *
 * @param rootService The root service to which this scene belongs
 */
class KabooStartMenuScene(val rootService: RootService) : MenuScene(1920, 1080), Refreshable {

    // This pane is used to hold all components of the scene and easily center them on the screen
    private val contentPane = Pane<UIComponent>(
        width = 700,
        height = 900,
        posX = 1920 / 2 - 700 / 2,
        posY = 1080 / 2 - 900 / 2,
        visual = ColorVisual(Color(0x0C2027))
    )

    // This label is used to display the title of the scene
    private val titleLabel = Label(
        text = "Kaboo",
        width = 700,
        height = 200,
        posX = 0,
        posY = 30,
        alignment = Alignment.CENTER,
        font = Font(100, Color(0xFFFFFFF), "JetBrains Mono ExtraBold")
    )


    // This text field is used to enter the name of the first player (default)
    private val player1Input = TextField(
        prompt = "Player 1",
        width = 400, height = 100,
        posX = 150, posY = 300,
        font = Font(30, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x49585D)),
    )

    // This text field is used to enter the name of the second player (default)
    private val player2Input = TextField(
        prompt = "Player 2",
        width = 400, height = 100,
        posX = 150, posY = 500,
        font = Font(30, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x49585D)),
    )

    // This button is used to exit the application
    val exitButton = Button(
        text = "EXIT",
        width = 280, height = 75,
        posX = 370, posY = 700,
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x1C5182C))
    )

    // This button is used to start the game
    private val startButton = Button(
        text = "START",
        width = 280, height = 75,
        posX = 50, posY = 700,
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold "),
        visual = ColorVisual(Color(0x1CABDAFF))
    ).apply {
        // When the button is clicked, the game is started with the entered player names
        onMouseClicked = {
            // If there are two player names, start the game
            val player1name = player1Input.text
            val player2name = player2Input.text
            if(player1name.isNotBlank() && player2name.isNotBlank()) {
                // Call the startGame method of the game service with the player names
                rootService.gameService.startGame(player1 = player1name, player2 = player2name)
            }
            else { rootService.gameService.startGame(player1 = "P1", player2 = "P2") }
        }
    }

    // Initialize the scene by setting the background color and adding all components to the content pane
    init {
        background = ColorVisual(Color(12, 32, 39, 240))
        contentPane.addAll(titleLabel,
            player1Input, player2Input,
            startButton, exitButton)
        addComponents(contentPane)
    }
}
