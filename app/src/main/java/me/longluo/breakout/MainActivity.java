package me.longluo.breakout;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import me.longluo.breakout.gl.MainRenderer;

/**
 * Requests an OpenGL SurfaceView and assigns a renderer to it.
 */
public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet;
    private MainRenderer mainRenderer;
    private int backButtonPressed = 0;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        glSurfaceView = new GLSurfaceView(this);

        // Request an OpenGL 2.0 compatible context
        glSurfaceView.setEGLContextClientVersion(2);

        mainRenderer = new MainRenderer(this);

        // Assign our renderer
        glSurfaceView.setRenderer(mainRenderer);
        rendererSet = true;

        // handle user input
        glSurfaceView.setOnTouchListener((v, event) -> {
            if (event != null) {
                final float normalizedX = (event.getX() / (float) v.getWidth()) * 2 - 1;
                final float normalizedY = -((event.getY() / (float) v.getHeight()) * 2 - 1);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    glSurfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            mainRenderer.handleTouchPress(normalizedX, normalizedY);
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    glSurfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            float normalizedX = (event.getX() / (float) v.getWidth()) * 2 - 1;
                            float normalizedY = -((event.getY() / (float) v.getHeight()) * 2 - 1);
                            mainRenderer.handleTouchDrag(normalizedX, normalizedY);
                        }
                    });
                }
                return true;
            } else {
                return false;
            }
        });

        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (rendererSet) {
            glSurfaceView.onPause();

            // allows to start over when resuming from pause
            mainRenderer.setHasStarted(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }

    @Override
    public void onBackPressed() {
        if (backButtonPressed > 0) {
            super.onBackPressed();
            mainRenderer.stop();
        } else {
            backButtonPressed++;
        }
    }
}
