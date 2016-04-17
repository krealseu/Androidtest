package org.kreal.akvideoplayer.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import org.kreal.akvideoplayer.decoder.CCMediaDecoder;

/**
 * Created by lthee on 2016/3/23.
 */
public class CCVideoView extends GLSurfaceView {
    private YuvRender mRender;
    private CCMediaDecoder mMediaDecoder;
    private Handler mHandler;
    private OnPreparedListener mOnPreparedListener;
    private boolean isPrepared = false;
    Runnable test = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(test, 41);
            if(mMediaDecoder.decoderOne()==1){
                requestRender();
                return;
            };
            mHandler.removeCallbacks(test);
        }
    };
    public CCVideoView(Context context) {
        super(context);
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        //fix for error No Config chosen, but I don't know what this does.
        super.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        // Set the Renderer for drawing on the GLSurfaceView
        mRender = new YuvRender(context);
        mMediaDecoder = new CCMediaDecoder();
        mHandler = new Handler();
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public CCVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        //fix for error No Config chosen, but I don't know what this does.
        super.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        // Set the Renderer for drawing on the GLSurfaceView
        mRender = new YuvRender(context);
        mMediaDecoder = new CCMediaDecoder();
        mHandler = new Handler();
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }



    public void setVideoPath(String path) {
        if(mMediaDecoder.setPath(path) == 0){
            mRender.setYUVdata(mMediaDecoder.getByteBuffer(), mMediaDecoder.getVideoWidth(), mMediaDecoder.getVideoHeight());
            if(mOnPreparedListener != null)
                mOnPreparedListener.onPrepared(this);
            isPrepared = true;
        };

    }

    public void start(){
        mHandler.post(test);
    }

    public void pause(){
        mHandler.removeCallbacks(test);
    };

    public void seekTo(int i){
        mMediaDecoder.seekTo(i);
    };

    public int getCurrentPosition(){
        if(isPrepared)
            return mMediaDecoder.getCurrentPosition();
        else
            return 0;
    };

    public int getDuration(){
        if(isPrepared)
            return mMediaDecoder.getDuration();
        else
            return 0;
    }

    public void setOnPreparedListener(OnPreparedListener l)
    {
        mOnPreparedListener = l;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        Log.i("sdf", "sdfasdfasdfasdf");
    }

    public interface OnPreparedListener
    {
        /**
         * Called when the media file is ready for playback.
         *
         * @param mp the MediaPlayer that is ready for playback
         */
        void onPrepared(CCVideoView mp);
    }

}
