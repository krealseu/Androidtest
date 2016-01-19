package com.example.kreal.opengl;

/**
 * Created by Kreal on 2015/9/13.
 */


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public abstract class GLWallpaperService extends WallpaperService {

    public class GLEngine extends Engine {
        class WallpaperGLSurfaceView extends GLSurfaceView {
            WallpaperGLSurfaceView(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }

            public void onDestroy() {
                super.onDetachedFromWindow();
            }
        }

        public WallpaperGLSurfaceView glSurfaceView;
        private boolean rendererHasBeenSet;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (rendererHasBeenSet) {
                if (visible) {
                    glSurfaceView.onResume();
                } else {
                    glSurfaceView.onPause();
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            glSurfaceView.onDestroy();
        }

        protected void setRenderer(Renderer renderer) {
            glSurfaceView.setRenderer(renderer);
            rendererHasBeenSet = true;
        }

        protected void setPreserveEGLContextOnPause(boolean preserve) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                glSurfaceView.setPreserveEGLContextOnPause(preserve);
            }
        }

        protected void setEGLContextClientVersion(int version) {
            glSurfaceView.setEGLContextClientVersion(version);
        }
        protected void setRenderMode(int renderMode) {
            glSurfaceView.setRenderMode(renderMode);
        }
        protected void requestRender() {
            glSurfaceView.requestRender();
        }
        protected int getHeight() {
            return glSurfaceView.getHeight();
        }
        protected int getWidth() {
            return glSurfaceView.getWidth();
        }
    }

}
