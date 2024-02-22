package me.longluo.breakout.gl;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import me.longluo.breakout.R;
import me.longluo.breakout.control.GameControl;
import me.longluo.breakout.model.Space;
import me.longluo.breakout.util.LoggerConfig;
import me.longluo.breakout.util.TextResourceReader;

/**
 * Makes render operations in OpenGL with matrix, colors, positions and shader language.
 */
public class MainRenderer implements GLSurfaceView.Renderer {

    private final String TAG = "MainRenderer";
    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";
    private static final String U_MATRIX = "u_Matrix";

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;

    private FloatBuffer vertexData;
    private final Context context;

    private int program;
    private int aPositionLocation;
    private int aColorLocation;

    private GameControl gameControl;

    private boolean hasStarted = false;

    public MainRenderer(Context context) {
        setVertexData(Space.VERTICES);
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }

        glUseProgram(program);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);

        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_POSITION_LOCATION.
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);

        glEnableVertexAttribArray(aPositionLocation);

        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_COLOR_LOCATION.
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);

        glEnableVertexAttribArray(aColorLocation);
        glLineWidth(5);

        gameControl = new GameControl(context);
    }

    /**
     * onSurfaceChanged is called whenever the surface has changed. This is
     * called at least once when the surface is initialized. Keep in mind that
     * Android normally restarts an Activity on rotation, and in that case, the
     * renderer will be destroyed and a new one created.
     *
     * @param width  The new width, in pixels.
     * @param height The new height, in pixels.
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

        if (width > height) {
            // Landscape
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            // Portrait or square
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        drawSpace();
        drawDefender();
        drawBall();
    }

    private void drawSpace() {
        setVertexData(Space.VERTICES);
        bindVertexData();
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }

    private void drawDefender() {
//        setVertexData(defenderControl.getCurrentDefenderVertices());
        setVertexData(gameControl.getCurrentDefenderVertices());
        bindVertexData();
        glDrawArrays(GL_LINES, 0, 2);
    }

    private void drawBall() {
//        setVertexData(ballControl.getCurrentBallVertices());
        setVertexData(gameControl.getCurrentBallVertices());
        bindVertexData();
        glDrawArrays(GL_POINTS, 0, 1);
    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        if (LoggerConfig.ON) {
            Log.d(TAG, "Action touch at point(" + normalizedX + ", " + normalizedY + ")");
        }
        // shoots the ball
        if (!hasStarted) {
            new Thread(gameControl).start();
            hasStarted = true;
        }
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {
        if (LoggerConfig.ON) {
            Log.d(TAG, "Action drag at point(" + normalizedX + ", " + normalizedY + ")");
        }
//        defenderControl.updateDefenderPosition(normalizedX);
//        ballControl.updateDefenderPosition(defenderControl.getDefenderPosition());
        gameControl.updateDefenderPosition(normalizedX);
    }

    /**
     * Sets the vertices of an object as a buffer.
     *
     * @param objectVertices contains the data that will be allocated.
     */
    private void setVertexData(float[] objectVertices) {
        vertexData = ByteBuffer
                .allocateDirect(objectVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(objectVertices);
    }

    /**
     * Tells OpenGL where to find the location of an attribute.
     */
    private void bindVertexData() {

        // Associates data with our attribute a_Position.
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_COLOR_LOCATION.
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);

        glEnableVertexAttribArray(aColorLocation);
    }

    /**
     * Allows MainActivity to restart the game after pause.
     */
    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public void stop() {
        gameControl.stop();
    }
}
