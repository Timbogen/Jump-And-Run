package Game;

import Game.Models.Blocks;
import Game.Models.Map;
import UI.Main;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class Player {
    /**
     * The gravity
     */
    public static final double GRAVITY = 2000;
    /**
     * The acceleration and the resistance sideways
     */
    public static final double ACCELERATION = 1200, RESISTANCE = 400;
    /**
     * The maximum velocity sideways
     */
    public static final double MAX_VELOCITY = 500;
    /**
     * The map the player is on
     */
    private final Map map;
    /**
     * The position of the player
     */
    public double x, y;
    /**
     * The measurements of the player
     */
    public double radius;
    /**
     * The game state
     */
    public boolean dead, won;
    /**
     * The current speed of the player
     */
    private double vx, vy;
    /**
     * The current acceleration of the player
     */
    private double ax;
    /**
     * True if the player is jumping
     */
    private boolean jumping = true;
    /**
     * The current rotation of the player
     */
    private double rotation = 0;

    /**
     * Constructor
     */
    public Player(Map map) {
        this.map = map;
        spawn();
    }

    /**
     * Spawn the player
     */
    public void spawn() {
        radius = (double) Map.SIZE / 2;
        dead = false;
        won = false;
        x = map.x;
        y = map.y;
    }

    /**
     * Start a normal jump
     */
    public void jump() {
        if (!jumping && map.getBlock(x, y + radius + 1).solid) {
            jumping = true;
            vy = -900;
        }
    }

    /**
     * Start a boost jump
     *
     * @param sign the direction
     */
    public void boostJump(int sign) {
        jumping = true;
        vy = sign * 1300;
    }

    /**
     * Start a boost jump sideways
     *
     * @param sign the direction
     */
    public void boostSide(int sign) {
        vx = sign * 800;
    }

    /**
     * Start the acceleration for the player
     */
    public void accelerate(double acceleration) {
        ax = acceleration;
    }

    /**
     * Move the player
     */
    public void move() {
        // Check if the player is still alive
        if (dead) return;

        // Move the player horizontally
        x = vx * Game.deltaTime + x;
        if (map.getBlock(x + radius, y).solid) {
            handleRightCollision();
        } else if (map.getBlock(x - radius, y).solid) {
            handleLeftCollision();
        }

        // Move the player vertically
        y = vy * Game.deltaTime + y;
        if (map.getBlock(x, y + radius).solid) {
            handleBottomCollision();
        }
        if (map.getBlock(x, y - radius).solid) {
            handleTopCollision();
        }

        // Check if the player won
        if (map.finish < x) {
            won = true;
            die(false, false);
        }

        // Respect the gravity
        vy += GRAVITY * Game.deltaTime;

        // Respect the current movement and the resistance
        double resistance = vx < 0 ? RESISTANCE : -RESISTANCE;
        vx += resistance * Game.deltaTime;
        if (Math.abs(vx) > MAX_VELOCITY) return;
        vx += ax * Game.deltaTime;
    }

    /**
     * Handle a collision
     */
    private void handleTopCollision() {
        // Reset the speed
        vy = 0;

        // Check for block collisions
        Blocks block = map.getBlock(x, y - radius);
        if (block.equals(Blocks.BOUNCE)) {
            boostJump(1);
        } else if (block.equals(Blocks.SPIKE)) {
            die(true, true);
        }

        // Correct the position
        while (map.getBlock(x, y - radius).solid) y++;
    }

    /**
     * Handle a collision
     */
    private void handleRightCollision() {
        // Reset the speed
        vx = 0;

        // Check for block collisions
        Blocks block = map.getBlock(x + radius, y);
        if (block.equals(Blocks.BOUNCE)) {
            boostSide(-1);
        } else if (block.equals(Blocks.SPIKE)) {
            die(true, true);
        }

        // Correct the position
        while (map.getBlock(x + radius, y).solid) x--;
    }

    /**
     * Handle a collision
     */
    private void handleBottomCollision() {
        // Reset the speed and the jumping flag
        jumping = false;
        vy = 0;

        // Check if the player hit the ground
        if ((int) (y + radius) / Map.SIZE >= 58) {
            die(false, true);
            return;
        }

        // Check for block collisions
        Blocks block = map.getBlock(x, y + radius);
        if (block.equals(Blocks.BOUNCE)) {
            boostJump(-1);
        } else if (block.equals(Blocks.SPIKE)) {
            die(true, true);
            return;
        }

        // Correct the position
        while (map.getBlock(x, y + radius).solid) y--;
    }

    /**
     * Handle a collision
     */
    private void handleLeftCollision() {
        // Reset the speed
        vx = 0;

        // Check for block collisions
        Blocks block = map.getBlock(x - radius, y);
        if (block.equals(Blocks.BOUNCE)) {
            boostSide(1);
        } else if (block.equals(Blocks.SPIKE)) {
            die(true, true);
        }

        // Correct the position
        while (map.getBlock(x - radius, y).solid) x++;
    }

    /**
     * Let the player die
     *
     * @param animation true if the player should be animated on death
     * @param respawn   true if the player shall respawn automatically
     */
    private void die(boolean animation, boolean respawn) {
        // Reset the player's speed and acceleration
        vx = 0;
        vy = 0;
        ax = 0;
        dead = true;

        // Respawn the player
        if (!respawn) return;
        if (animation) {
            new AnimationTimer() {
                @Override
                public void handle(long l) {
                    if (radius < Main.WIDTH) {
                        radius += 10;
                    } else {
                        spawn();
                        stop();
                    }
                }
            }.start();
        } else {
            spawn();
        }
    }

    /**
     * Draw the player
     *
     * @param g the graphics context
     */
    public void drawPlayer(GraphicsContext g) {
        // Draw the player
        g.setFill(Color.DARKORANGE);
        g.fillOval(x - radius, (int) (y - radius), 2 * radius, 2 * radius);

        // Check if the player won
        if (won) {
            // Draw the background
            g.setFill(new Color(0, 0, 0, 0.4));
            g.fillRect(x - Main.WIDTH, 0, 2 * Main.WIDTH, Main.HEIGHT);

            // Draw the title
            g.setFill(Color.WHITE);
            g.setFont(new Font("Verdana", 120));
            g.setTextAlign(TextAlignment.CENTER);
            g.setTextBaseline(VPos.CENTER);
            g.fillText("Victory", x, Main.HALF_HEIGHT - 100);

            // Draw the hint on how to continue
            g.setFont(new Font("Verdana", 60));
            g.fillText("Press SPACE to continue", x, Main.HALF_HEIGHT + 60);
        }

        // Check if the player's dead
        if (dead) return;

        // Calculate the current rotation of the player
        double angular_velocity = vx / radius;
        rotation += angular_velocity * Game.deltaTime;

        // Draw the smaller circle
        double cx = x - 8 * Math.cos(rotation), cy = (int) y - 8 * Math.sin(rotation);
        double radius = 5;
        g.setFill(Color.BLACK);
        g.fillOval(cx - radius, cy - radius, 2 * radius, 2 * radius);
    }
}
