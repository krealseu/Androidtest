package com.example.kreal.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Kreal on 2015/9/14.
 */
public class testrender implements GLSurfaceView.Renderer {
    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;
    /** This will be used to pass in model position information. */
    private int mPositionHandle;
    /** This will be used to pass in model color information. */
    private int mColorHandle;
    private int programHandle;


    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private int program;
    private int attribPosition;
    private int attribTexCoord;
    private int uniformTexture;
    private Context mcontext=null;
    int[] textureId = new int[1];
    public void setMcontext(Context mcontext) {
        this.mcontext = mcontext;
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        loadVertex();
        initShader();
        loadTexture();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
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

    @Override
    public void onDrawFrame(GL10 gl) {
        // clear screen to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        vertex.position(0);
// load the position
// 3(x , y , z)
// (2 + 3 )* 4 (float size) = 20
        GLES20.glVertexAttribPointer(attribPosition,
                3, GLES20.GL_FLOAT,
                false, 20, vertex);
        vertex.position(3);
// load the texture coordinate
        GLES20.glVertexAttribPointer(attribTexCoord,
                2, GLES20.GL_FLOAT,
                false, 20, vertex);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT,
                index);

    }
    private void loadVertex() {

// float size = 4
        this.vertex = ByteBuffer.allocateDirect(quadVertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        this.vertex.put(quadVertex).position(0);
// short size = 2
        this.index = ByteBuffer.allocateDirect(quadIndex.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        this.index.put(quadIndex).position(0);
    }
    private FloatBuffer vertex;
    private ShortBuffer index;
    private float[] quadVertex = new float[] {
            -0.5f, 0.5f, 0.0f, // Position 0
            0, 1.0f, // TexCoord 0
            -0.5f, -0.5f, 0.0f, // Position 1
            0, 0, // TexCoord 1
            0.5f , -0.5f, 0.0f, // Position 2
            1.0f, 0, // TexCoord 2
            0.5f, 0.5f, 0.0f, // Position 3
            1.0f, 1.0f, // TexCoord 3
    };
    private short[] quadIndex = new short[] {
            (short)(0), // Position 0
            (short)(1), // Position 1
            (short)(2), // Position 2
            (short)(2), // Position 2
            (short)(3), // Position 3
            (short)(0), // Position 0
    };
    private void initShader() {

        String vertexSource =("VertexShader.glsl");
        String fragmentSource = ("FragmentShader.glsl");
// Load the shaders and get a linked program
        program = loadProgram(vertexSource, fragmentSource);
// Get the attribute locations
        attribPosition = GLES20.glGetAttribLocation(program, "a_position");
        attribTexCoord = GLES20.glGetAttribLocation(program, "a_texCoord");
        uniformTexture = GLES20.glGetUniformLocation(program,
                "u_samplerTexture");
        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(attribPosition);
        GLES20.glEnableVertexAttribArray(attribTexCoord);
// Set the sampler to texture unit 0
        GLES20.glUniform1i(uniformTexture, 0);
    }
    public static int loadProgram(String vertexSource, String
            fragmentSource) {

// Load the vertex shaders
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexSource);
// Load the fragment shaders
        int fragmentShader =loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentSource);
// Create the program object
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Error create program.");
        }
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
// Link the program
        GLES20.glLinkProgram(program);
        int[] linked = new int[1];
// Check the link status
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0) {
            GLES20.glDeleteProgram(program);
            throw new RuntimeException("Error linking program: " +
                    GLES20.glGetProgramInfoLog(program));
        }
// Free up no longer needed shader resources
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
        return program;
    }
    public static int loadShader(int shaderType, String source) {

// Create the shader object
        int shader = GLES20.glCreateShader(shaderType);
        if (shader == 0) {
            throw new RuntimeException("Error create shader.");
        }
        int[] compiled = new int[1];
// Load the shader source
        GLES20.glShaderSource(shader, source);
// Compile the shader
        GLES20.glCompileShader(shader);
// Check the compile status
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("Error compile shader: " +
                    GLES20.glGetShaderInfoLog(shader));
        }
        return shader;
    }
    public  void loadTexture() {


// Generate a texture object
        GLES20.glGenTextures(1, textureId, 0);
        int[] result = null;
        if (textureId[0] != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.aa);

            result = new int[3];

// Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
// Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);
// Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
// Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        } else {
            throw new RuntimeException("Error loading texture.");
        }

    }
}
