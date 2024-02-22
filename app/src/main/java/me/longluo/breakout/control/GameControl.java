package me.longluo.breakout.control;

import android.content.Context;

import me.longluo.breakout.model.Defender;
import me.longluo.breakout.model.Point;
import me.longluo.breakout.model.Space;
import me.longluo.breakout.util.SoundManager;


/**
 * Controls ball, defender, sound effects and provides data for renderization.
 */
public class GameControl implements Runnable {

    private static final float BALL_DOWN = -2f;

    private int numberOfDefenses;
    private boolean gameOver;
    private boolean beginFromRightSide;

    private BallControl ballControl;
    private DefenderControl defenderControl;
    private SoundManager soundManager;

    public GameControl(Context context) {
        defenderControl = new DefenderControl();
        ballControl = new BallControl(context);
        soundManager = new SoundManager(context);
        numberOfDefenses = 0;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Side bounds collision
            if (ballControl.getBall().getPosition().x < Space.LEFT_BOUND || ballControl.getBall().getPosition().x > Space.RIGHT_BOUND) {
                ballControl.changeBallDirectionX();
                if (ballControl.getBall().getPosition().y > Space.LOW_BOUND) {
                    soundManager.playWallCollision();
                }
            }

            // Up bound collision
            if (ballControl.getBall().getPosition().y > Space.UP_BOUND) {
                ballControl.changeBallDirectionY();
                soundManager.playWallTopCollision();
            }
            // Low bound position
            // If there is no collision with the defender, game over
            if (ballControl.getBall().getPosition().y < Space.LOW_BOUND) {
                if (!gameOver) {
                    if (wasDefended()) {
                        soundManager.playDefenderCollision();
                        ballControl.changeBallDirectionY();
                        defenseUpdate();
                    } else {
                        gameOver = true;
                    }
                }
            }
            // Game is already over. Ends the thread loop after waiting the ball to leave the screen.
            // This condition allows the ball to be drawn util it leaves the screen.
            if (ballControl.getBall().getPosition().y < BALL_DOWN) {
                break;
            }

            // Can have two types of initial direction
            if (!beginFromRightSide) {
                ballControl.changeBallDirectionX();
                beginFromRightSide = true;
            }
            ballControl.getBall().setPosition(new Point(
                    ballControl.getBall().getPosition().x + (ballControl.getBallSpeed() * ballControl.getBallDirectionX()),
                    ballControl.getBall().getPosition().y + (ballControl.getBallSpeed() * ballControl.getBallDirectionY())));
        }
    }

    public float[] getCurrentBallVertices() {
        return ballControl.getCurrentBallVertices();
    }

    public float[] getCurrentDefenderVertices() {
        return defenderControl.getCurrentDefenderVertices();
    }

    public void updateDefenderPosition(float normalizedX) {
        defenderControl.updateDefenderPosition(normalizedX);
    }

    /**
     * Checks if the defender position collides with the ball.
     */
    private boolean wasDefended() {
        if (ballControl.getBall().getPosition().x <= (defenderControl.getDefenderPosition().x + Defender.HALF_LENGTH) &&
                ballControl.getBall().getPosition().x >= (defenderControl.getDefenderPosition().x - Defender.HALF_LENGTH)) {
            return true;
        }
        return false;
    }

    private void defenseUpdate() {
        numberOfDefenses++;
        if (numberOfDefenses % 2 == 0) {
            ballControl.boost();
            soundManager.playBoost();
        } else {
            soundManager.playDefenderCollision();
        }
    }

    public void stop() {
        soundManager.stop();
    }
}
