package UI;

import Game.Game;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;

public class Controller {

    /**
     * The canvas
     */
    @FXML
    Canvas canvas;

    /**
     * Start the game
     */
    @FXML
    public void initialize() {
        canvas.setFocusTraversable(true);
        new Game(canvas);
    }
}
