package UI;

import Game.MapGenerator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     * The size of the window
     */
    public static int WIDTH = 1000, HEIGHT = 600, HALF_WIDTH = 500, HALF_HEIGHT = 300;

    /**
     * Main
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Setup and show the primary stage
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Start generating the map
        MapGenerator.generateMap();

        // Show the scene
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Jump and Run");
        primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
        primaryStage.show();
    }
}
