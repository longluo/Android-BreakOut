package me.longluo.breakout.control;

import android.content.Context;

import me.longluo.breakout.model.Ball;

public class BallControl {

    public static final float BALL_ACCELERATION = 1.25f;
    private float ballSpeed = 0.005f;

    private Ball ball;
    private int ballDirectionY;
    private int ballDirectionX;

    public BallControl(Context context) {
        ball = new Ball();
        ballDirectionY = 1;
        ballDirectionX = 1;
    }

    public void changeBallDirectionX() {
        ballDirectionX *= -1;
    }

    public void changeBallDirectionY() {
        ballDirectionY *= -1;
    }

    public float[] getCurrentBallVertices() {
        return new float[]{ball.getPosition().x, ball.getPosition().y,
                Ball.RGB, Ball.RGB, Ball.RGB};
    }

    public Ball getBall() {
        return ball;
    }

    public int getBallDirectionY() {
        return ballDirectionY;
    }

    public int getBallDirectionX() {
        return ballDirectionX;
    }

    public float getBallSpeed() {
        return ballSpeed;
    }

    public void boost() {
        ballSpeed *= BallControl.BALL_ACCELERATION;
    }
}