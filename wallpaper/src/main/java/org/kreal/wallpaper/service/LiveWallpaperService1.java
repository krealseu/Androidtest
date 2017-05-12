package org.kreal.wallpaper.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by lthee on 2017/4/27.
 */

public class LiveWallpaperService1 extends GLWallpaperService1 {
    final static private String TAG = GLWallpaperService.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new SwitchEngice();
    }

    class SSEngine extends Engine{
        private boolean mVisible = false;
        private long REFRESHTIME = 1000 * 1 * 10;
        private long lastthime = SystemClock.elapsedRealtime();
        private String fileroot = "/sdcard/CC/BiZhi";
        private String nowWallpaper = "";

        private int screenWidth;
        private int screenHeight;
        private int photoWidth;
        private int photoHeight;
        private float mScale = 1f;
        private int sreenNum = 1;
        private int xoff = 0;
        private Bitmap bitmap = null;
        private void changepaper() {
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
            updateScale();
            bitmap = BitmapFactory.decodeFile(fileroot+File.separator+nowWallpaper);
        }

        private boolean isNeedRefresh() {
            long time = SystemClock.elapsedRealtime();
            if (Math.abs(time - lastthime) > REFRESHTIME) {
                lastthime = time;
                return true;
            } else return false;
        }

        private void updateScale(){
        }


        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            changepaper();
        }

        @Override
        public void onSurfaceRedrawNeeded(SurfaceHolder holder) {
            super.onSurfaceRedrawNeeded(holder);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.i(TAG,"change");
            GLES20.glViewport(0, 0, width, height);
            screenHeight = height;
            screenWidth =width;
            updateScale();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.mVisible = visible;
            if (visible)
                if (isNeedRefresh()) {
                    changepaper();
                }
        }
        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            int all = (int) (photoWidth*mScale-screenWidth);
            all = all>screenWidth/3?screenWidth/3:all;
            xoff = (int) (all * (0.5-xOffset));
            SurfaceHolder surfaceHolder= getSurfaceHolder();
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawBitmap(bitmap,0,0,new Paint());
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    class SwitchEngice extends GLEngine{
        @Override
        GLEngineView onCreateGLEngineView() {
            return new SwitchEngineView(getApplicationContext());
        }

        class SwitchEngineView extends GLEngineView implements GLSurfaceView.Renderer {
            private boolean mVisible = false;
            private Photo mPhoto;
            private long REFRESHTIME = 1000 * 600;
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

            public SwitchEngineView(Context context) {
                super(context);
                setEGLContextClientVersion(2);
                setRenderer(this);
                setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            }

            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
                mPhoto = new Photo();
                changeWallpaper();
//                mPhoto.setPhoto(fileroot + File.separator + nowWallpaper);
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                Log.i(TAG,"onSurfaceChanged");
                GLES20.glViewport(0, 0, width, height);
                screenHeight = height;
                screenWidth =width;
                mPhoto.setViewport(width, height);
                updateScale();
//                requestRender();
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                if (!mVisible)
                    return;
                Log.i(TAG,"draw");
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
                mPhoto.draw(gl, xoff, 0, mScale);
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
    }
}
