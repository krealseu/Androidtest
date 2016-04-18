package org.kreal.akvideoplayer.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.VideoView;

import org.kreal.akvideoplayer.R;
import org.kreal.akvideoplayer.utli.DebugUtil;

/**
 * Created by lthee on 2016/1/26.
 */
public class VideoPlayer extends FrameLayout {
    private static final String TAG = "AKVideoPlay";
    private Context mContext;
    private MediaControler mMediaControl;
    private VideoView mVideoView;
    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    
    private final Runnable mUpdataSkbRunnable=new Runnable() {
        @Override
        public void run() {
            DebugUtil.LogInfo(TAG,"update");
            if(mMediaControl.getPannelVisisable()==VISIBLE) {
                int CurrentTime = mVideoView.getCurrentPosition();
                int AllTime = mVideoView.getDuration();
                mMediaControl.updateSeekbar(1.0f * CurrentTime / AllTime);
                mMediaControl.setCurrrentTime(CurrentTime);
                mHander.postDelayed(mUpdataSkbRunnable, 100);
            }
        }
    };

    private final Runnable mHidePannelRunable = new Runnable() {
        @Override
        public void run() {
            mHander.removeCallbacks(mUpdataSkbRunnable);
            mMediaControl.setPannelVisiable(INVISIBLE);
        }
    };

    private final Runnable mShowPannelRunable = new Runnable() {
        @Override
        public void run() {
            mMediaControl.setPannelVisiable(VISIBLE);
            mHander.post(mUpdataSkbRunnable);
        }
    };
    private final Runnable mHidePlayIvRunable = new Runnable() {
        @Override
        public void run() {

        }
    };


    private MediaControler.MediaControlImpl mMediaControlImpl = new MediaControler.MediaControlImpl()   {
        @Override
        public void goBack() {
            mHander.post(mHidePannelRunable);
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onplay() {
            mVideoView.start();
            mHander.post(mHidePannelRunable);
        }

        @Override
        public void onPause() {
            mVideoView.pause();
            mHander.post(mShowPannelRunable);
        }


        @Override
        public void onProgressTurn(MediaControler.ProgressState state, float progress) {
            if (state == MediaControler.ProgressState.DOING)
                Log.i(TAG, "" + progress);
        }

        @Override
        public void onPannelControl(MediaControler.PannelControl state, float dist) {

        }
    };

    public VideoPlayer(Context context) {
        super(context);
        initView(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        View.inflate(mContext, R.layout.layout_video_player, this);
        mMediaControl = (MediaControler) findViewById(R.id.MediaControler);
        mVideoView = (VideoView)findViewById(R.id.VideoView);

        mVideoView.setOnPreparedListener(onPreparedListener);
        mMediaControl.setMediaControl(mMediaControlImpl);
    }

    public void playUrl(Uri uri){
        mVideoView.setVideoURI(uri);
        mHander.post(mUpdataSkbRunnable);
    }

    public void resumePlay(Uri uri,int position){
    }

    private MediaPlayer.OnPreparedListener onPreparedListener=new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //mp.start();
            //mMediaControl.setAlltime(mp.getDuration());
        }
    };
}
