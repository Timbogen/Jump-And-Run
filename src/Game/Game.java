package Game;

import UI.Main;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;

public class Game {

    /**
     * The delta time (in seconds)
     */
    public static double deltaTime = 0;
    /**
     * The last time the game was drawn (in nanoseconds)
     */
    private static long lastTime;
    /**
     * The canvas
     */
    private final Canvas canvas;
    /**
     * The graphics context of the canvas
     */
    private final GraphicsContext g;
    /**
     * The player
     */
    private Player player;
    /**
     * The game timer
     */
    private AnimationTimer gameTimer;

    /**
     * Constructor
     */
    public Game(Canvas canvas)
    {
        // Get the graphics context
        this.canvas = canvas;
        this.g = canvas.getGraphicsContext2D();

        // Start the game
        start();

        // Set up the key events
        setupControls();
    }

    /**
     * Start the game
     */
    private void start()
    {
        // Wait for the map to be generated
        MapGenerator.onFinishedLoading((map -> {
            // Create the player
            this.player = new Player(map);

            // Start drawing the game
            gameTimer = new AnimationTimer() {
                public void handle(long currentNanoTime)
                {
                    // Get the time
                    if (lastTime == 0) lastTime = currentNanoTime - 5000;
                    deltaTime = (double) (currentNanoTime - lastTime) / 1e9;

                    // Move the player
                    player.move();

                    // Draw the map and the player
                    moveCamera(g);
                    map.drawMap(g, player);
                    player.drawPlayer(g);

                    lastTime = currentNanoTime;
                }
            };
            gameTimer.start();
        }));
    }

    /**
     * Move the camera
     *
     * @param g the graphics context
     */
    private void moveCamera(GraphicsContext g)
    {
        // Clear the graphics
        g.clearRect(-g.getTransform().getTx(), 0, Main.WIDTH, Main.HEIGHT);

        // Calculate the new x position
        double targetX = Main.HALF_WIDTH - player.x;
        double x = g.getTransform().getTx();
        x += 4 * (targetX - x) * deltaTime;

        // Move the camera
        g.setTransform(new Affine(new Translate(x, 0)));
    }

    /**
     * Setup the controls
     */
    private void setupControls()
    {
        // When the key is pressed
        canvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE && player.won) {
                gameTimer.stop();
                MapGenerator.generateMap();
            } else if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
                player.accelerate(Player.ACCELERATION);
            } else if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) {
                player.accelerate(-Player.ACCELERATION);
            } else if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) {
                player.jump();
            }
        });

        // When the key is released
        canvas.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
                player.accelerate(0);
            } else if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) {
                player.accelerate(0);
            } else if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) {
                player.jump();
            }
        });
    }
}
