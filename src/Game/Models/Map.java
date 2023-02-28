package Game.Models;

import Game.Player;
import UI.Main;
import javafx.scene.canvas.GraphicsContext;

public class Map {
    /**
     * The size of a field
     */
    public static final int SIZE = 30;
    /**
     * The actual height of the map
     */
    public static final int HEIGHT = 20, HEIGHT_OFFSET = 10;
    /**
     * The size of the map
     */
    public int width, height;
    /**
     * The actual map
     */
    public int[][] map;
    /**
     * The spawn point
     */
    public int x, y;
    /**
     * The horizontal distance to the finish
     */
    public int finish;

    /**
     * Constructor
     */
    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        map = new int[height][width];
    }

    /**
     * Draw the map
     *
     * @param g the graphics context
     * @param p the player
     */
    public void drawMap(GraphicsContext g, Player p) {
        // Calculate the render range
        int startIndex = (int) ((p.x - 1.5 * Main.HALF_WIDTH) / SIZE);
        startIndex = Math.max(startIndex, 1);
        int endIndex = startIndex + (int) (1.5 * Main.WIDTH / SIZE);
        endIndex = Math.min(endIndex, width - 1);

        // Draw the map
        for (int m = HEIGHT_OFFSET; m < HEIGHT + HEIGHT_OFFSET; m++) {
            for (int n = startIndex; n < endIndex; n++) {
                Blocks.values()[map[m][n]].draw(m - HEIGHT_OFFSET, n - 1, getNeighbors(m, n), g);
            }
        }
    }

    /**
     * Find the block on the position
     *
     * @param x position
     * @param y position
     * @return the right block
     */
    public Blocks getBlock(double x, double y) {
        try {
            return Blocks.values()[map[(int) (y / SIZE) + HEIGHT_OFFSET][(int) (x / SIZE) + 1]];
        } catch (Exception e) {
            return Blocks.NORMAL;
        }
    }

    /**
     * Get the neighbors of a certain block
     *
     * @param m position of the block
     * @param n position of the block
     * @return a matrix containing the neighbors
     */
    private int[][] getNeighbors(int m, int n) {
        return new int[][]{{map[m - 1][n - 1], map[m - 1][n], map[m - 1][n + 1]}, {map[m][n - 1], map[m][n], map[m][n + 1]}, {map[m + 1][n - 1], map[m + 1][n], map[m + 1][n + 1]}};
    }
}
