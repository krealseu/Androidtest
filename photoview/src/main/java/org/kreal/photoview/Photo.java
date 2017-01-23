package org.kreal.photoview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by lthee on 2016/9/9.
 */
public class Photo {
    private float mViewWidth = 1;
    private float mViewHeight = 1;
    private float mPhotoWidth = 1;
    private float mPhotoHeight = 1;
    private int[] Position = new int[2];
    private int[] mTexture = new int[1];
    private float[] mMatrix = new float[16*3];
    private int mProgramHandle;
    private int mPositionHandle;
    private int mTexPositionHandle;
    private int mTextureHandle;
    private int mViewMatrixHandle;
    final private String vertexShader =
            "uniform mat4 u_ViewMatrix[3];"+
            "attribute vec4 position;"+
            "attribute vec2 texturePosition;"+
             "varying vec2 vTextureCoord;"+
            "void main() {"+
                "vTextureCoord=texturePosition;"+
                 "gl_Position = u_ViewMatrix[0]*u_ViewMatrix[1]*u_ViewMatrix[2]*position;"+
            "}";
    final private String fragmentShader =
            "precision mediump float;" +
            "varying vec2 vTextureCoord;" +
            "uniform sampler2D sTexture;" +
            "void main() {" +
            "  gl_FragColor = texture2D(sTexture,vTextureCoord);" +
            "}";
    public Photo() {
        mProgramHandle=GLhelp.loadProgram(vertexShader, fragmentShader);
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "position");
        mTexPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "texturePosition");
        mTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, "sTexture");
        mViewMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_ViewMatrix");
        GLES20.glUniform1i(mTextureHandle, 0);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glEnableVertexAttribArray(mTexPositionHandle);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glGenTextures(1, mTexture, 0);
        float[] position = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f,  1.0f,
                1.0f,  1.0f
        };
        float[] texcoord = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f,  0.0f,
                1.0f,  0.0f,
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

        Matrix.setIdentityM(mMatrix,0);
        Matrix.setIdentityM(mMatrix,16);
        Matrix.setIdentityM(mMatrix,32);

    }

    public void draw(GL10 gl){
        GLES20.glUseProgram(mProgramHandle);
        GLES20.glUniformMatrix4fv(mViewMatrixHandle,3,false,mMatrix,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glUseProgram(0);
    }

    public void draw(GL10 gl,int x,int y,float a){
        GLES20.glUseProgram(mProgramHandle);
        Matrix.setIdentityM(mMatrix,16);
        Matrix.rotateM(mMatrix,16,a,0,0,1);
        GLES20.glUniformMatrix4fv(mViewMatrixHandle,3,false,mMatrix,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glUseProgram(0);
    }

    public boolean setPhoto(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if(bitmap==null)
            return false;
        setPhoto(bitmap);
        bitmap.recycle();
        return true;
    }

    public boolean setPhoto(Bitmap bitmap){
        if(bitmap==null)
            return false;
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR) ;
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        mPhotoWidth = bitmap.getWidth();
        mPhotoHeight = bitmap.getHeight();
        setViewPosMatrix();
        return true;
    }

    public void setViewRatio(int width, int height) {
        mViewWidth = width;
        mViewHeight = height;
        setViewPosMatrix();
    }

    private void setViewPosMatrix(){
        if(mPhotoWidth*mViewHeight<mViewWidth*mPhotoHeight){
            mMatrix[0] = mViewHeight/mViewWidth;
            mMatrix[5] = 1;
            mMatrix[32+0]= mPhotoWidth/mPhotoHeight;
            mMatrix[32+5]= 1;
        }
        else{
            mMatrix[0] = 1;
            mMatrix[5]= mViewWidth/mViewHeight;
            mMatrix[32+0]= 1;
            mMatrix[32+5]=mPhotoHeight/mPhotoWidth;
        }
    }

}
