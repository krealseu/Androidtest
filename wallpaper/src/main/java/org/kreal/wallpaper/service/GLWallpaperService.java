package org.kreal.wallpaper.service;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

/**
 * Created by lthee on 2017/5/3.
 */

public abstract class GLWallpaperService extends WallpaperService {
    class GLEngine extends Engine{
        GLEngineView mGLSurfaceView =  new GLEngineView(getApplicationContext());
        class GLEngineView extends GLSurfaceView{
            public GLEngineView(Context context) {
                super(context);
            }
            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }
        }
        public void setRenderer(Renderer render){
            mGLSurfaceView.setRenderer(render);
        }
        public void setEGLContextClientVersion(int version){
            mGLSurfaceView.setEGLContextClientVersion(version);
        }
        public void setRenderMode(int renderMode){
            mGLSurfaceView.setRenderMode(renderMode);
        }
        public void requestRender(){
            mGLSurfaceView.requestRender();
        }
        void queueEvent(Runnable r){
            mGLSurfaceView.queueEvent(r);
        }
    }
}
