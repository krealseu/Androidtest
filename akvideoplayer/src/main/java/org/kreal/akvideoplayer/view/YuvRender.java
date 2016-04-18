package org.kreal.akvideoplayer.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import org.kreal.akvideoplayer.utli.GLhelp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by lthee on 2016/3/23.
 */
public class YuvRender implements GLSurfaceView.Renderer {
    Context context;
    private int mPositionHandle;
    private int mColorHandle;
    private int[] Position = new int[2];
    private int uniformY;
    private int uniformU;
    private int uniformV;
    private int[] YUVtexture = new int[3];
    private ByteBuffer YUVdata;
    private int YUVWidth;
    private int YUVHeight;
    private float YUVRatio;
    private float viewRatio;

    public YuvRender(Context context){
        this.context = context;
    }

    public void setsize(){
        float[] position = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f,  1.0f,
                1.0f,  1.0f
        };
        float rat=YUVRatio/viewRatio;
        if(rat>1){
            //Log.i("ccFFmpeg","1  "+YUVRatio+ "+" +viewRatio);
            position[1] = -1/rat;
            position[3] = -1/rat;
            position[5] = 1/rat;
            position[7] = 1/rat;
        }
        else {
            //Log.i("ccFFmpeg","2   "+1/rat);
            position[0] = -rat;
            position[2] = rat;
            position[4] = -rat;
            position[6] = rat;
        }
        FloatBuffer floatBuffer0 = ByteBuffer.allocateDirect(position.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer0.put(position).position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, Position[0]);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, floatBuffer0.capacity() * 4, floatBuffer0);
    }

    public void setYUVdata(ByteBuffer buffer){
        YUVdata = buffer;
    }

    public void setYUVdata(ByteBuffer buffer,int YUVWidth,int YUVHeight){
        YUVdata = buffer;
        this.YUVWidth = YUVWidth;
        this.YUVHeight = YUVHeight;
        this.YUVRatio = 1.0f*YUVWidth/YUVHeight;
        setsize();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        String fragmentShader = GLhelp.readfromAssets(context, "FragmentShaderYUV.glsl");
        String vertexShader=GLhelp.readfromAssets(context,"VertexShaderYUV.glsl");
        int programHandle=GLhelp.loadProgram(vertexShader, fragmentShader);
        GLES20.glUseProgram(programHandle);
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "position");
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "texCoord");
        uniformY = GLES20.glGetUniformLocation(programHandle, "SamplerY");
        uniformU = GLES20.glGetUniformLocation(programHandle, "SamplerU");
        uniformV = GLES20.glGetUniformLocation(programHandle, "SamplerV");
        GLES20.glUniform1i(uniformY, 0);
        GLES20.glUniform1i(uniformU, 1);
        GLES20.glUniform1i(uniformV, 2);
        GLES20.glGenTextures(3, YUVtexture, 0);
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
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, floatBuffer0.capacity() * 4, floatBuffer0, GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, Position[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, floatBuffer1.capacity() * 4, floatBuffer1, GLES20.GL_STATIC_DRAW);
//        YUVWidth=640;
//        YUVHeight=360;
//        YUVdata = ByteBuffer.allocateDirect(640 * 360 * 3 / 2).order(ByteOrder.nativeOrder());
//        try {
//            FileInputStream fileInputStream=new FileInputStream("/sdcard/sintel_640_360.yuv");
//            byte[] data = new byte[640*360*3/2];
//            fileInputStream.read(data);
//            YUVdata.put(data);
//            Log.i("sdf", "sdf" + viewRatio);
//            FileOutputStream outputStream = new FileOutputStream("/sdcard/sd.yuv");
//            outputStream.write(data);
//            outputStream.close();
//            fileInputStream.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        viewRatio = 1.0f*width/height;
        setsize();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.5f, 0.2f, 0.4f, 1.f);
        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, Position[0]);
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, Position[1]);
        GLES20.glVertexAttribPointer(mColorHandle, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        if(YUVdata == null){
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            return;
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, YUVtexture[0]);
        YUVdata.position(0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, YUVWidth, YUVHeight, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, YUVdata);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR) ;

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, YUVtexture[1]);
        YUVdata.position(YUVWidth * YUVHeight);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, YUVWidth / 2, YUVHeight / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, YUVdata);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR) ;

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, YUVtexture[2]);
        YUVdata.position(YUVWidth*YUVHeight*5/4);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, YUVWidth/2, YUVHeight/2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, YUVdata);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR) ;

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
