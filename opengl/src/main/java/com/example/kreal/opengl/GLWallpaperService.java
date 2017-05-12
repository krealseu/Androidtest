package com.example.kreal.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


abstract class GLWallpaperService extends WallpaperService {
    abstract class GLEngine extends Engine {
        private GLEngineView mGLEngineView;
        boolean mHasSurface = false;
        abstract GLEngineView onCreateGLEngineView();
        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            mHasSurface = true;
            mGLEngineView = onCreateGLEngineView();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mGLEngineView.surfaceDestroyed(holder);
            mGLEngineView = null;
            mHasSurface = false;
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
//            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            mGLEngineView.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
//            super.onVisibilityChanged(visible);
            if (!mHasSurface || mGLEngineView == null)
                return;
            mGLEngineView.onVisibilityChanged(visible);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
//            mGLEngineView.onTouchEvent(event);
        }

        class GLEngineView extends GLSurfaceView {
            public GLEngineView(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }

            public void onVisibilityChanged(boolean visible) {
            }

            public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            }
        }

    }

}
