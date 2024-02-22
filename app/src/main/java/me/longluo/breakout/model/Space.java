package me.longluo.breakout.model;

/**
 * Represents the available space where the ball can move.
 */
public class Space {

    public static final float[] VERTICES = {
            // Order of coordinates: X, Y, R, G, B
            // Triangle Fan

            // game space
            0f, 0f, 0.2f, 0.2f, 0.3f,
            -1f, -1f, 0.2f, 0.2f, 0.3f,
            1f, -1f, 0.2f, 0.2f, 0.3f,
            1f, 1.5f, 0.2f, 0.2f, 0.4f,
            -1f, 1.5f, 0.2f, 0.2f, 0.4f,
            -1f, -1f, 0.2f, 0.2f, 0.3f,
    };

    public static final float LEFT_BOUND = -1f;
    public static final float RIGHT_BOUND = 1f;
    public static final float UP_BOUND = 1.5f;
    public static final float LOW_BOUND = -1f;
}
