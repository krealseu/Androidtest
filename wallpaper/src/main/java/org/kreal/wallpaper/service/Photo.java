package org.kreal.wallpaper.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by lthee on 2016/9/9.
 */
public class Photo {
    final static private String TAG = Photo.class.getSimpleName();
    static final private String vertexShader =
            "uniform mat4 u_MVPMatrix ;" +
                    "attribute vec2 a_Postion;" +
                    "attribute vec2 a_TextureCoordinates;" +
                    "varying vec2 v_TextureCoord;" +
                    "void main() {" +
                    "v_TextureCoord=a_TextureCoordinates;" +
                    "gl_Position = u_MVPMatrix * vec4(a_Postion,0,1);" +
                    "}";
    static final private String fragmentShader =
            "precision mediump float;" +
                    "varying vec2 v_TextureCoord;" +
                    "uniform sampler2D s_Texture;" +
                    "uniform float u_Alphe;" +
                    "void main() {" +
                    " vec4 texture = texture2D(s_Texture,v_TextureCoord);" +
                    "texture.a = u_Alphe;" +
                    "  gl_FragColor = texture;" +
                    "}";
    static final private String U_MVPMATRIX = "u_MVPMatrix";
    static final private String A_POSOTIION = "a_Postion";
    static final private String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    static final private String U_ALPHE = "u_Alphe";
    static final private String S_TEXTURE = "s_Texture";

    private int[] Position = new int[2];
    private int[] mTexture = new int[1];
    //0 mProjectionMatrix   1 mModelMatrix  2 photo
    private float[] mMatrix = new float[16 * 3];
    private int mProgramHandle;
    private int mPositionHandle;
    private int mTexPositionHandle;
    private int mTextureHandle;
    private int mAlpheHandle;
    private int mViewMatrixHandle;

    private boolean mHasSetPhoto = false;

    static public int TYPE_CROP = 1;
    static public int TYPE_100 = 2;
    static public int TYPE_FIT = 3;
    static public int TYPE_STRETCH = 4;

    private float mViewWidth = 1;
    private float mViewHeight = 1;
    private int mPhotoWidth = 1;
    private int mPhotoHeight = 1;


    public Photo() {
        initGLProgram();
    }

    public Photo(String path) {
        initGLProgram();
        setPhoto(path);
    }

    private void initGLProgram() {
        //创建程序
        mProgramHandle = GLhelp.loadProgram(vertexShader, fragmentShader);
        //获取gl的变量句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, A_POSOTIION);
        mTexPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, A_TEXTURE_COORDINATES);
        mTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, S_TEXTURE);
        mViewMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, U_MVPMATRIX);
        mAlpheHandle = GLES20.glGetUniformLocation(mProgramHandle, U_ALPHE);
        //
        GLES20.glUniform1i(mTextureHandle, GLES20.GL_TEXTURE);           //  ?????

        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        //开启 混合模式 透明度处理
//        GLES20.glEnable(GLES20.GL_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //生成纹理ID
        GLES20.glGenTextures(1, mTexture, 0);
        if (mTexture[0] == 0)
            throw new RuntimeException("Error Loading Textue");
        //顶点数据 输入
        GLES20.glEnableVertexAttribArray(mTexPositionHandle);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        float[] position = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f
        };
        float[] texcoord = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
        };
        FloatBuffer floatBuffer0 = ByteBuffer.allocateDirect(position.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer0.put(position).position(0);
        FloatBuffer floatBuffer1 = ByteBuffer.allocateDirect(texcoord.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer1.put(texcoord).position(0);
        GLES20.glGenBuffers(2, Position, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, Position[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, floatBuffer0.capacity() * 4, floatBuffer0, GLES20.GL_STATIC_DRAW);
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, Position[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, floatBuffer1.capacity() * 4, floatBuffer1, GLES20.GL_STATIC_DRAW);
        GLES20.glVertexAttribPointer(mTexPositionHandle, 2, GLES20.GL_FLOAT, false, 0, 0);
        //初始矩阵
        Matrix.setIdentityM(mMatrix, 0);
        Matrix.setIdentityM(mMatrix, 16);
        Matrix.setIdentityM(mMatrix, 32);
    }

    /**
     * 绘制图形
     *
     * @param gl 绘制的GL句柄
     */
    public void draw(GL10 gl) {
        draw(gl, 0, 0, 1f, 0f, 1f);
    }

    /**
     * 绘制图形
     *
     * @param gl
     * @param x     图形x平移
     * @param y     图形y平移
     * @param scale 图形缩放
     */
    public void draw(GL10 gl, float x, float y, float scale) {
        draw(gl, x, y, scale, 0f, 1f);
    }

    /**
     * @param gl
     * @param x
     * @param y
     * @param scale
     * @param rotate
     * @param alphe
     */
    public void draw(GL10 gl, float x, float y, float scale, float rotate, float alphe) {
        if (!mHasSetPhoto)
            return;
        //启用程序
        GLES20.glUseProgram(mProgramHandle);
        //旋转 平移 缩放
        Matrix.setIdentityM(mMatrix, 16);
        Matrix.rotateM(mMatrix, 16, rotate, 0, 0, 1);
        Matrix.translateM(mMatrix, 16, x * 2f, y * 2f, 0);
        Matrix.scaleM(mMatrix, 16, scale, scale, 1);

        Matrix.multiplyMM(mMatrix, 16, mMatrix, 16, mMatrix, 32);
        Matrix.multiplyMM(mMatrix, 16, mMatrix, 0, mMatrix, 16);

        GLES20.glUniformMatrix4fv(mViewMatrixHandle, 1, false, mMatrix, 16);

        //透明度
        GLES20.glUniform1f(mAlpheHandle, alphe);
        //画图 绑定纹理   避免和其他的 gl程序冲突
        GLES20.glActiveTexture(GLES20.GL_TEXTURE);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture[0]);
        //画图
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glUseProgram(0);
    }

    /**
     * must call in GL Thread
     *
     * @param filepath photo filepath
     * @return
     */
    public boolean setPhoto(String filepath) {
        mHasSetPhoto = false;
//        mHasLoadTexture = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filepath);
        if (bitmap == null) {
            return false;
        }
        loadtexture(bitmap);
        mPhotoWidth = bitmap.getWidth();
        mPhotoHeight = bitmap.getHeight();
        mHasSetPhoto = true;
        bitmap.recycle();
        bitmap = null;
        setPhotoPosMatrix();
        return true;
    }

    public void setViewport(int width, int height) {
        this.mViewWidth = width;
        this.mViewHeight = height;
        setViewPosMatrix();
    }

    private void loadtexture(Bitmap bitmap) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture[0]);
        //纹理过滤
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    private void setViewPosMatrix() {
        mMatrix[0] = 1f / mViewWidth;
        mMatrix[5] = 1f / mViewHeight;
    }

    private void setPhotoPosMatrix() {
        mMatrix[32 + 0] = mPhotoWidth;
        mMatrix[32 + 5] = mPhotoHeight;
    }

    public int getmPhotoWidth() {
        return mPhotoWidth;
    }

    public int getmPhotoHeight() {
        return mPhotoHeight;
    }
}
