package Game;

import Game.Models.Blocks;
import Game.Models.Map;

import java.util.Random;

public class MapGenerator {

    /**
     * The height of a map
     */
    private static final int HEIGHT = 70;
    /**
     * Margin to the start and the end of a map
     */
    private static final int MARGIN = 20;
    /**
     * The distances after a field was generated
     */
    public static final int MAX_HEIGHT = 6, MAX_HEIGHT_BOUNCE = 10;
    /**
     * The random class
     */
    private static final Random random = new Random();
    /**
     * True if the map is loaded
     */
    private static boolean loaded = false;
    /**
     * The map to be generated
     */
    private static Map map;
    /**
     * The callback when the generator finished the map
     */
    private static OnFinishedLoading action;
    /**
     * The height of the last block that was placed
     */
    private static int lastHeight;

    /**
     * Start generating the map in a new thread
     */
    public static void generateMap() {
        new Thread(() -> {
            // Initialize the map
            map = new Map(random(300, 500), HEIGHT);
            setBorder();
            setStart();

            // Generate the main part
            generateMain();

            // Update the loading flag
            loaded = true;

            // Call the callback
            if (action != null) action.onFinishedLoading(map);
        }).start();
    }

    /**
     * Set the border of the map
     */
    private static void setBorder() {
        for (int m = 0; m < HEIGHT; m++) {
            map.map[m][0] = Blocks.NORMAL.ordinal();
            map.map[m][map.width - 1] = Blocks.NORMAL.ordinal();
        }
        for (int n = 0; n < map.width; n++) {
            map.map[0][n] = Blocks.NORMAL.ordinal();
            map.map[HEIGHT - 1][n] = Blocks.NORMAL.ordinal();
        }
    }

    /**
     * Set the start platforms
     */
    private static void setStart() {
        map.x = (MARGIN + 3) * Map.SIZE;
        map.y = 50;
        map.finish = (map.width - MARGIN - 3) * Map.SIZE;

        // Set the start platform
        lastHeight = random(12, 16);
        for (int n = MARGIN; n < MARGIN + 6; n++) {
            map.map[lastHeight + Map.HEIGHT_OFFSET][n] = Blocks.NORMAL.ordinal();
        }

        // Set the end platform
        for (int n = map.width - MARGIN - 6; n < map.width - MARGIN; n++) {
            map.map[18 + Map.HEIGHT_OFFSET][n] = Blocks.NORMAL.ordinal();
        }
    }

    /**
     * Generate the main part of the map
     */
    private static void generateMain() {
        // The parameters
        int maxHeight = MAX_HEIGHT;
        int currentHeight = lastHeight - maxHeight;

        for (int n = MARGIN + 6; n < map.width - MARGIN - 6; n++) {
            // Check if the next field has to be a platform
            if (currentHeight >= 20) {
                n += generatePlatform(Blocks.NORMAL.ordinal(), n, currentHeight);
                lastHeight = currentHeight;
                maxHeight = MAX_HEIGHT;
            } else if (currentHeight < 4) {
                maxHeight--;
            } else {
                // Make a random decision
                int random = random(0, 7);
                if (random < 5) {
                    maxHeight--;
                } else if (random < 7) {
                    n += generatePlatform(Blocks.NORMAL.ordinal(), n, currentHeight);
                    maxHeight = MAX_HEIGHT;
                    lastHeight = currentHeight;
                } else if (random < 8) {
                    currentHeight = random(15, 19);
                    n += generatePlatform(Blocks.BOUNCE.ordinal(), n, currentHeight);
                    maxHeight = MAX_HEIGHT_BOUNCE;
                    lastHeight = currentHeight;
                } else {
                    maxHeight--;
                }
            }
            // Update the current height
            currentHeight = lastHeight - maxHeight;
        }
    }

    /**
     * Generate a platform
     *
     * @param type   of the platform
     * @param start  vertical start of the platform
     * @param height of the platform
     * @return the length of the platform
     */
    private static int generatePlatform(int type, int start, int height) {
        // Generate the length
        int length = random(3, 10);
        while (start + length > map.width - MARGIN - 6) {
            length--;
        }

        // Set the platform
        for (int n = start; n < start + length; n++) {
            map.map[height + Map.HEIGHT_OFFSET][n] = type;
        }

        // Check if spikes could be inserted
        if (length > 7 && random(1, 2) == 1) {
            for (int n = start + random(2, 4); n < start + length - random(2, 4); n++) {
                map.map[height + Map.HEIGHT_OFFSET][n] = Blocks.SPIKE.ordinal();
            }
        }
        return length;
    }

    /**
     * Set the event for the map generation finish
     *
     * @param action to be executed
     */
    public static void onFinishedLoading(OnFinishedLoading action) {
        if (loaded) {
            action.onFinishedLoading(map);
        }
        MapGenerator.action = action;
    }

    /**
     * Get a random number
     *
     * @param min value
     * @param max value
     * @return the random number
     */
    private static int random(int min, int max)
    {
        return random.nextInt(max - min + 1) + min;
    }

    /***
     * The interface for on finished event
     */
    public interface OnFinishedLoading {
        void onFinishedLoading(Map map);
    }
}
