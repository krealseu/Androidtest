package org.kreal.wallpaper.service;


import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by lthee on 2017/5/3.
 */

public class LiveWallpaperService extends GLWallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new SwitchEngice2();
    }
    class SwitchEngice extends GLEngine implements GLSurfaceView.Renderer{
        final private String TAG = SwitchEngice.class.getSimpleName();
        private boolean mVisible = false;
        private Photo mPhoto;
        private Photo mBackPhoto;
        private long REFRESHTIME = 1000 * 1;
        private long lastthime = SystemClock.elapsedRealtime();
        private String fileroot = "/sdcard/CC/BiZhi";
        private String nowWallpaper = "";

        private int screenWidth;
        private int screenHeight;
        private int photoWidth;
        private int photoHeight;
        private float mScale = 1f;
        private int xoff = 0;
        private void changeWallpaper() {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    File paperfolder = new File(fileroot);
                    String[] papers = paperfolder.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            return filename.endsWith(".jpg") || filename.endsWith(".png");
                        }
                    });
                    if (papers == null)
                        return;
                    int i;
                    do {
                        i = new Random().nextInt(papers.length);
                    } while (nowWallpaper.matches(papers[i]));
                    nowWallpaper = papers[i];
                    mPhoto.setPhoto(fileroot + File.separator + nowWallpaper);
                    photoHeight = mPhoto.getmPhotoHeight();
                    photoWidth = mPhoto.getmPhotoWidth();
                    xoff = 0;
                    updateScale();
                }
            });
        }

        private boolean isNeedRefresh() {
            long time = SystemClock.elapsedRealtime();
            if (Math.abs(time - lastthime) > REFRESHTIME) {
                lastthime = time;
                return true;
            } else return false;
        }

        private void updateScale(){
            if (mPhoto.getmPhotoWidth() * screenHeight > mPhoto.getmPhotoHeight() * screenWidth)
                mScale = 1f*screenHeight / mPhoto.getmPhotoHeight();
            else mScale = 1f*screenWidth / mPhoto.getmPhotoWidth();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setEGLContextClientVersion(2);
            setRenderer(this);
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
            mPhoto = new Photo();
            mBackPhoto = new Photo();
            changeWallpaper();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.i(TAG,"onSurfaceChanged");
            GLES20.glViewport(0, 0, width, height);
            screenHeight = height;
            screenWidth =width;
            mPhoto.setViewport(width, height);
            mBackPhoto.setViewport(width, height);
            updateScale();
        }
        long animationStartTime ;
        @Override
        public void onDrawFrame(GL10 gl) {
            if (!mVisible)
                return;
            Log.i(TAG,"draw");
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            if(true)
                mPhoto.draw(gl, xoff, 0, mScale);
            else {
                long delt = SystemClock.currentThreadTimeMillis() - animationStartTime;
                if (delt > 1000){
                    mBackPhoto.draw(gl,xoff,0,mScale);
                    Photo temp = mPhoto;
                    mPhoto = mBackPhoto;
                    mBackPhoto = temp;
                    temp = null;
                }
                else {
                    mPhoto.draw(gl, xoff, 0, mScale);
                    mBackPhoto.draw(gl,xoff,0,mScale,0,delt/1000f);
                }
                requestRender();
            }
        }
        @Override
        public void onVisibilityChanged(boolean visible) {
            this.mVisible = visible;
            if (visible)
                if (isNeedRefresh()) {
                    changeWallpaper();
                    requestRender();
                }
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            int all = (int) (photoWidth*mScale-screenWidth);
            all = all>screenWidth/3?screenWidth/3:all;
            xoff = (int) (all * (0.5-xOffset));
            Log.i(TAG,"ll"+xOffset);
            requestRender();
        }
    }
    class SwitchEngice2 extends GLEngine implements GLSurfaceView.Renderer{
        final private String TAG = SwitchEngice2.class.getSimpleName();
        private boolean mVisible = false;

        private PhotoInfo mFrontPhoto = new PhotoInfo();
        private PhotoInfo mBackPhoto = new PhotoInfo();

        private long REFRESHTIME = 1000 * 600;
        private long lastthime = SystemClock.elapsedRealtime();
        private String fileroot = "/sdcard/CC/BiZhi";

        private long animationStartTime ;
        private boolean isNeedDoAnimation = false;
        private long ANIMATIONTIME = 1000;


        private int screenWidth;
        private int screenHeight;

        private void changeWallpaper() {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    File paperfolder = new File(fileroot);
                    String[] papers = paperfolder.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            return filename.endsWith(".jpg") || filename.endsWith(".png");
                        }
                    });
                    if (papers == null)
                        return;
                    int i;
                    do {
                        i = new Random().nextInt(papers.length);
                    } while (mFrontPhoto.name.matches(papers[i]));

                    mBackPhoto.name = papers[i];
                    mBackPhoto.photo.setPhoto(fileroot + File.separator + mBackPhoto.name);
                    mBackPhoto.width = mBackPhoto.photo.getmPhotoWidth();
                    mBackPhoto.xoff = 0;
                    updateScale(mBackPhoto);

                    isNeedDoAnimation = true ;
                    animationStartTime = SystemClock.elapsedRealtime();
                }
            });
        }

        private boolean isNeedRefresh() {
            long time = SystemClock.elapsedRealtime();
            if (Math.abs(time - lastthime) > REFRESHTIME) {
                lastthime = time;
                return true;
            } else return false;
        }


        private void updateScale(PhotoInfo photoInfo){
            if (photoInfo.photo.getmPhotoWidth() * screenHeight > photoInfo.photo.getmPhotoHeight() * screenWidth)
                photoInfo.scale = 1f*screenHeight / photoInfo.photo.getmPhotoHeight();
            else photoInfo.scale = 1f*screenWidth / photoInfo.photo.getmPhotoWidth();
            photoInfo.deltX = (int) (photoInfo.width*photoInfo.scale-screenWidth);
            photoInfo.deltX = photoInfo.deltX>screenWidth/3?screenWidth/3:photoInfo.deltX;
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setEGLContextClientVersion(2);
            setRenderer(this);
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
            mFrontPhoto.photo = new Photo();
            mBackPhoto.photo = new Photo();
            changeWallpaper();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.i(TAG,"onSurfaceChanged");
            GLES20.glViewport(0, 0, width, height);
            screenHeight = height;
            screenWidth =width;
            mFrontPhoto.photo.setViewport(width, height);
            mBackPhoto.photo.setViewport(width, height);
            updateScale(mBackPhoto);
            updateScale(mFrontPhoto);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            if (!mVisible)
                return;
            Log.i(TAG,"draw");
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            if (!isNeedDoAnimation)
                mFrontPhoto.photo.draw(gl, mFrontPhoto.xoff, 0, mFrontPhoto.scale);
            else {
                long delt = SystemClock.elapsedRealtime() - animationStartTime;
                if (delt<30){
                    requestRender();
                    return;
                }
                if (delt > ANIMATIONTIME){
                    PhotoInfo temp = mFrontPhoto;
                    mFrontPhoto = mBackPhoto;
                    mBackPhoto = temp ;
                    temp = null;
                    mFrontPhoto.photo.draw(gl, mFrontPhoto.xoff, 0, mFrontPhoto.scale);
                    isNeedDoAnimation = false;
                }
                else {
                    mFrontPhoto.photo.draw(gl, mFrontPhoto.xoff, 0, mFrontPhoto.scale);
                    mBackPhoto.photo.draw(gl, mBackPhoto.xoff, 0, mBackPhoto.scale,0,(float) delt/ANIMATIONTIME);
                }
                requestRender();
            }
        }
        @Override
        public void onVisibilityChanged(boolean visible) {
            this.mVisible = visible;
            if (visible)
                if (isNeedRefresh()) {
                    changeWallpaper();
                    requestRender();
                }
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            mFrontPhoto.xoff = (int) (mFrontPhoto.deltX * (0.5-xOffset));
            mBackPhoto.xoff = (int) (mBackPhoto.deltX * (0.5-xOffset));
            Log.i(TAG,"ll"+xOffset);
            requestRender();
        }

        class PhotoInfo{
            public String name = "";
            public Photo photo;
            public int width;
            public float scale;
            public int xoff;
            public int deltX;
        }

    }
}
