package org.kreal.photoview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by lthee on 2016/9/7.
 */
public class GLPhotoView extends GLSurfaceView implements GLSurfaceView.Renderer{
    private PhotoRender mRender;
    private Photo mPhoto;
    private Photo mPhoto2;
    private void init(Context context){
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
//        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setRenderer(this);
//        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        requestRender();
    }


    public GLPhotoView(Context context) {
        super(context);
        init(context);
    }

    public GLPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
//        Log.i("aas","csasd");
        mPhoto = new Photo();
        mPhoto2 = new Photo();
        mPhoto2.setPhoto("/sdcard/333.png");
        mPhoto.setPhoto("/sdcard/123.png");

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        mPhoto.setViewport(width,height);
        mPhoto2.setViewport(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//        Log.i("df","draw");
//        mPhoto.setPhoto("/sdcard/123.png");
        mPhoto2.draw(gl,80,0,1f);
        mPhoto.draw(gl,280,0,1f,0,0.5f);
//        mPhoto2.setPhoto("/sdcard/333.png");
    }

}
