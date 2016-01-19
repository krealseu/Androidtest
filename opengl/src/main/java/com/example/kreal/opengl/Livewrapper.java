package com.example.kreal.opengl;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.IBinder;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class Livewrapper extends GLWallpaperService {
    @Override
    public Engine onCreateEngine() {

        return new OpenGLES2Engine();
    }

    class OpenGLES2Engine extends GLWallpaperService.GLEngine {
        // Check if the system supports OpenGL ES 2.0.
        private final float TOUCH_SCALE_FACTOR = 180.0f / 640;
        private float mPreviousX;
        private float mPreviousY;
        private LessonOneRenderer myrender;
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            // Check if the system supports OpenGL ES 2.0.
            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

            if (supportsEs2)
            {
                // Request an OpenGL ES 2.0 compatible context.
                setEGLContextClientVersion(2);

                // On Honeycomb+ devices, this improves the performance when
                // leaving and resuming the live wallpaper.
                setPreserveEGLContextOnPause(true);
                myrender=new LessonOneRenderer();
                // Set the renderer to our user-defined renderer.
                setRenderer( myrender);
                glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

            }
            else
            {
                // This is where you could create an OpenGL ES 1.x compatible
                // renderer if you wanted to support both ES 1 and ES 2.
                return;
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            requestRender();
        }
    }
}
