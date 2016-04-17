package com.example.kreal.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.opengl.GLSurfaceView;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by Kreal on 2015/9/13.
 */
public class LessonOneRenderer implements GLSurfaceView.Renderer {
    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] mProjectionMatrix = new float[16];

    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];

    /** Store our model data in a float buffer. */
    private final FloatBuffer mTriangle1Vertices;
    private final FloatBuffer mTriangle2Vertices;
    private final FloatBuffer mTriangle3Vertices;
    private final FloatBuffer mSquare;
    int texture;
    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;
    private int mTextureCoordHandle;
    /** This will be used to pass in model position information. */
    private int mPositionHandle;

    /** This will be used to pass in model color information. */
    private int mColorHandle;

    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

    /** How many elements per vertex. */
    private final int mStrideBytes = 7 * mBytesPerFloat;

    /** Offset of the position data. */
    private final int mPositionOffset = 0;

    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;

    /** Offset of the color data. */
    private final int mColorOffset = 3;

    /** Size of the color data in elements. */
    private final int mColorDataSize = 4;

    private Context mcontext=null;
    public void setMcontext(Context mcontext) {
        this.mcontext = mcontext;
    }
    /**
     * Initialize the model data.
     */
    public LessonOneRenderer()
    {
        // Define points for equilateral triangles.

        // This triangle is red, green, and blue.
        final float[] triangle1VerticesData = {
                // X, Y, Z,
                // R, G, B, A
                -1.0f, -1.0f, 0.0f,
                1.0f, 1.1f, 1.0f, 1.0f,

               1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,

                1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,

                -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                0.0f,0.0f,
                1.0f,0.0f,
                1.0f,1.0f,
                0.0f,1.0f,
        };

        // This triangle is yellow, cyan, and magenta.
        final float[] triangle2VerticesData = {
                // X, Y, Z,
                // R, G, B, A
                -0.5f, -0.25f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f,

                0.5f, -0.25f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f,

                0.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f,

                0.0f,0.0f, 10.0f,0.0f,5f,10.0f
        };

        // This triangle is white, gray, and black.
        final float[] triangle3VerticesData = {
                // X, Y, Z,
                // R, G, B, A
                0.0f,0.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,

                1.0f, 0.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f,

                0.9848f,0.173648f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f};
        final float[] lines = {
                // X, Y, Z,
                // R, G, B, A
               -1.0f,-1.0f,1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,

                -1.0f,-1.0f,-1.0f,
                0.0f, 0.5f, 0.5f, 1.0f,

                1.0f,-1.0f,1.0f,
                0.0f, 0.5f, 0.5f, 1.0f,

                1.0f,-1.0f,-1.0f,
                0.0f, 0.5f, 0.5f, 1.0f,

                1.0f,1.0f,1.0f,
                0.0f, 0.5f, 0.5f, 1.0f,

                1.0f,1.0f,-1.0f,
                0.0f, 0.5f, 0.5f, 1.0f,

                -1.0f,1.0f,1.0f,
                0.0f, 0.5f, 0.5f, 1.0f,

                -1.0f,1.0f,-1.0f,
                0.0f, 0.5f, 0.5f, 1.0f,

                -1.0f,-1.0f,1.0f,
                0.0f, 0.5f, 0.5f, 1.0f,

                -1.0f,-1.0f,-1.0f,
                0.0f, 0.5f, 0.5f, 1.0f,

                -1.0f,1.0f,-1.0f,
                0.0f, 0.5f, 0.5f, 1.0f,
                1.0f,-1.0f,-1.0f,
                0.0f, 0.5f, 0.5f, 1.0f,
                1.0f,1.0f,-1.0f,
                0.0f, 0.5f, 0.5f, 1.0f,

        };

        // Initialize the buffers.
        mTriangle1Vertices = ByteBuffer.allocateDirect(triangle1VerticesData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangle2Vertices = ByteBuffer.allocateDirect(triangle2VerticesData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangle3Vertices = ByteBuffer.allocateDirect(triangle3VerticesData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mSquare=ByteBuffer.allocateDirect(lines.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        mTriangle1Vertices.put(triangle1VerticesData).position(0);
        mTriangle2Vertices.put(triangle2VerticesData).position(0);
        mTriangle3Vertices.put(triangle3VerticesData).position(0);
        mSquare.put(lines).position(0);
    }
    public int textureId;
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        // Set the background clear color to gray.
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 2.0f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 1.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);


        String fragmentShader =GLhelp.readfromAssets(mcontext,"FragmentShader.glsl");
        String vertexShader=GLhelp.readfromAssets(mcontext,"VertexShader.glsl");
       int programHandle=GLhelp.loadProgram(vertexShader,fragmentShader);

        // Set program handles. These will later be used to pass in values to the program.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
        mTextureCoordHandle=GLES20.glGetAttribLocation(programHandle,"aTextureCoord");
                // Tell OpenGL to use this program when rendering.
                GLES20.glUseProgram(programHandle);
        //设置纹理采样器的值
//绑定纹理单元（为上面的采样器绑定）
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 0);
        Bitmap bitmap=BitmapFactory.decodeResource(mcontext.getResources(),R.drawable.aa);
        texture=GLhelp.loadTexture(bitmap);

    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

    }


    int a=0;
    float x=0.0f;
    float y=0.0f;
    public void setxy(float X,float Y){
        x+=X;y+=Y;
    }
    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Draw the triangle facing straight on.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0, 0, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawTriangle(mTriangle1Vertices);

        // Draw one translated a bit down and rotated to be flat on the ground.
//        Matrix.setIdentityM(mModelMatrix, 0);
//        Matrix.translateM(mModelMatrix, 0, 0.0f, -1.0f, 0.0f);
//        Matrix.rotateM(mModelMatrix, 0, 90.0f, 1.0f, 0.0f, 0.0f);
//        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);

//        Matrix.setIdentityM(mModelMatrix, 0);
//        Matrix.rotateM(mModelMatrix, 0, 90.0f, 1.0f, 0.0f, 0.0f);
//        Matrix.rotateM(mModelMatrix, 0, angleInDegrees*0.5f, 0.0f, 1.0f, 0.0f);
//        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 0.0f, 0.0f);

//        drawSquare(mSquare);
    }

    /**
     * Draws a triangle from the given vertex data.
     *
     * @param aTriangleBuffer The buffer containing the vertex data.
     */
    private void drawTriangle(final FloatBuffer aTriangleBuffer)
    {
        // Pass in the position information
        aTriangleBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, aTriangleBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        aTriangleBuffer.position(28);
        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false,8, aTriangleBuffer);
        // Pass in the color information
        aTriangleBuffer.position(mColorOffset);
//        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false, mStrideBytes, aTriangleBuffer);
//        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
    }
    private void drawSquare(final FloatBuffer aTriangleBuffer)
    {
        // Pass in the position information
        aTriangleBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        aTriangleBuffer.position(mColorOffset);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 13);
    }
}
