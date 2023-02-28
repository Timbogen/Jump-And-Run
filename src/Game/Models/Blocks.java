package Game.Models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public enum Blocks {

    /**
     * A normal air block
     */
    AIR(false, ((m, n, neighbors, g) -> {
    })),
    /**
     * A solid normal black block
     */
    NORMAL(true, (m, n, neighbors, g) -> {
        g.setFill(Color.BLACK);
        g.fillRect(n * Map.SIZE - 1, m * Map.SIZE - 1, Map.SIZE + 2, Map.SIZE + 2);
    }),
    /**
     * A bounce block that provides the player a jump boost
     */
    BOUNCE(true, (m, n, neighbors, g) -> {
        g.setFill(Color.DARKBLUE);
        g.fillRect(n * Map.SIZE - 1, m * Map.SIZE - 1, Map.SIZE + 2, Map.SIZE + 2);
    }),
    /**
     * A spike block that kills the player
     */
    SPIKE(true, (m, n, neighbors, g) -> {
        g.setFill(Color.DARKRED);
        g.fillRect(n * Map.SIZE - 1, m * Map.SIZE - 1, Map.SIZE + 2, Map.SIZE + 2);
    });

    /**
     * The action for drawing a block
     */
    private final OnDraw draw;
    /**
     * True if the block is solid
     */
    public final boolean solid;

    /**
     * Constructor
     */
    Blocks(boolean solid, OnDraw draw)
    {
        this.solid = solid;
        this.draw = draw;
    }

    /**
     * Draw a block
     *
     * @param m         position of the block
     * @param n         position of the block
     * @param neighbors of the block
     * @param g         the graphics context
     */
    public void draw(int m, int n, int[][] neighbors, GraphicsContext g)
    {
        draw.onDraw(m, n, neighbors, g);
    }

    /**
     * Event for drawing a block
     */
    public interface OnDraw {
        void onDraw(int m, int n, int[][] neighbors, GraphicsContext g);
    }
}
