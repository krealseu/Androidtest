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

    class OpenGLES2Engine extends GLEngine {
        @Override
        GLEngineView onCreateGLEngineView() {
            return new dd(getApplicationContext());
        }
        class dd extends GLEngineView{
            private LessonOneRenderer mRenderer;

            public dd(Context context) {
                super(context);
                // Create an OpenGL ES 2.0 context.
                setEGLContextClientVersion(2);
                //fix for error No Config chosen, but I don't know what this does.
                super.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
                // Set the Renderer for drawing on the GLSurfaceView
                mRenderer = new LessonOneRenderer();
                mRenderer.setMcontext(getContext());
                setRenderer(mRenderer);
            }
        }
    }
}
