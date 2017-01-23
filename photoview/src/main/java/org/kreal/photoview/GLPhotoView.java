package org.kreal.photoview;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by lthee on 2016/9/7.
 */
public class GLPhotoView extends GLSurfaceView implements GLSurfaceView.Renderer{
    private PhotoRender mRender;
    private Photo mPhoto;
    private void init(Context context){
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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
        mPhoto = new Photo();
        mPhoto.setPhoto("/sdcard/123.png");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        mPhoto.setViewRatio(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mPhoto.draw(gl);
    }

}
