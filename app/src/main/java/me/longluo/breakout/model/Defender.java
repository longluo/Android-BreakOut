package me.longluo.breakout.model;

public class Defender {

    public static final float HALF_LENGTH = 0.2f;
    public static final float RGB = 1.0f;

    private Point position;

    public Defender() {
        position = new Point(0f, -1f);
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Point getPosition() {
        return position;
    }
}
