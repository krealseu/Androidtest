package org.kreal.akvideoplayer.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import org.kreal.akvideoplayer.R;
import org.kreal.akvideoplayer.model.VideoInfo;
import org.kreal.akvideoplayer.view.MediaControler.PlayState;

/**
 * Created by lthee on 2016/1/27.
 */
public class AkVideoPlayer2 extends Fragment {

    private static final String TAG = "AKVideoPlay";
    private View rootView;
    private Context mContext;
    private MediaControler mMediaControl;
    private CCVideoView mVideoView;
    private VideoInfo videoInfo = null;

    private int updateSkbDuration = 500;

    private PlayState mPlayState = PlayState.PLAY;

    private int mReusmeTime=0;
    private PlayState mResumePlayState = PlayState.PLAY;


    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private final Runnable mUpdataSkbRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "mUpdataSkbRunnable" + updateSkbDuration);
            int CurrentTime = 1000*mVideoView.getCurrentPosition();
            int AllTime = mVideoView.getDuration();
            mMediaControl.updateSeekbar(1.0f * CurrentTime / AllTime);
            if (mMediaControl.getPannelVisisable() == View.VISIBLE && mPlayState == PlayState.PLAY)
                mHander.postDelayed(mUpdataSkbRunnable, updateSkbDuration);
        }
    };

    private final Runnable mUpdateTimeRunable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "mUpdateTimeRunable");
            mMediaControl.setCurrrentTime(1000*mVideoView.getCurrentPosition());
            if (mMediaControl.getPannelVisisable() == View.VISIBLE && mPlayState == PlayState.PLAY)
                mHander.postDelayed(mUpdateTimeRunable, 1000);
        }
    };

    private final Runnable mHidePannelRunable = new Runnable() {
        @Override
        public void run() {
            hideorshowStateBar(false);
            mHander.removeCallbacks(mUpdataSkbRunnable);
            mMediaControl.setPannelVisiable(View.INVISIBLE);
        }
    };

    private final Runnable mHideSmallPannelRunable = new Runnable() {
        @Override
        public void run() {
            mMediaControl.setSmallPannelVisiable(mPlayState, View.INVISIBLE);
        }
    };

    private final Runnable mShowPannelRunable = new Runnable() {
        @Override
        public void run() {
            hideorshowStateBar(true);
            mMediaControl.setPannelVisiable(View.VISIBLE);
            updateAll();
        }
    };

    private final Runnable mShowSmallPannelRunable = new Runnable() {
        @Override
        public void run() {
            mMediaControl.setSmallPannelVisiable(mPlayState,View.VISIBLE);
        }
    };
    private void updateAll() {
        mHander.post(mUpdataSkbRunnable);
        mHander.post(mUpdateTimeRunable);
    }

    private void removeUpdate() {
        mHander.removeCallbacks(mUpdateTimeRunable);
        mHander.removeCallbacks(mUpdataSkbRunnable);
    }

    private void hideorshowStateBar(boolean is){
        if(is){
            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }else {
            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    private MediaControler.MediaControlImpl mMediaControlImpl = new MediaControler.MediaControlImpl() {
        @Override
        public void goBack() {
            //ShellUtil.execCommand("screencap -p /sdcard/mmm.png",false);
            getActivity().finish();
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onplay() {
            mVideoView.start();
            mPlayState = PlayState.PLAY;
            if(mMediaControl.getPannelVisisable()==View.VISIBLE)
                mHander.post(mHidePannelRunable);
            mHander.post(mHideSmallPannelRunable);
        }

        @Override
        public void onPause() {
            mVideoView.pause();
            mPlayState = PlayState.PAUSE;
            mHander.post(mShowPannelRunable);
            mHander.post(mShowSmallPannelRunable);
            removeUpdate();
        }


        @Override
        public void onProgressTurn(MediaControler.ProgressState state, float progress) {
            if (state == MediaControler.ProgressState.START) {
                mVideoView.pause();
                mPlayState = PlayState.PAUSE;
                mHander.removeCallbacks(mHideSmallPannelRunable);
                mHander.post(mHideSmallPannelRunable);
                mHander.removeCallbacks(mHidePannelRunable);
                //mHander.removeCallbacks(mHideSmallPannelRunable);
            } else if (state == MediaControler.ProgressState.DOING) {
                mMediaControl.setCurrrentTime(1000*mVideoView.getCurrentPosition());
                mVideoView.seekTo((int) (mVideoView.getDuration() * progress)/1000);
            } else if (state == MediaControler.ProgressState.STOP) {
                mVideoView.start();
                mPlayState = PlayState.PLAY;
                mHander.post(mShowSmallPannelRunable);
                mHander.postDelayed(mHidePannelRunable, 4000);
                mHander.postDelayed(mHideSmallPannelRunable,4000);
                updateAll();
            }
        }

        @Override
        public void onPannelControl(MediaControler.PannelControl state, float dist) {
            if (state == MediaControler.PannelControl.SINGLETAP) {
                if (mMediaControl.getPannelVisisable() == View.VISIBLE) {
                    mHander.post(mHidePannelRunable);
                    if(mPlayState==PlayState.PLAY)
                       mHander.post(mHideSmallPannelRunable);
                    else mHander.post(mShowSmallPannelRunable);
                }
                else {
                    mHander.post(mShowPannelRunable);
                    mHander.post(mShowSmallPannelRunable);
                }
            }else if(state == MediaControler.PannelControl.DOUBLETAP){
                if(mPlayState == PlayState.PLAY || mPlayState == PlayState.PREPARING){
                    mVideoView.pause();
                    mPlayState = PlayState.PAUSE;
                    mHander.post(mShowSmallPannelRunable);
                }else if(mPlayState == PlayState.PAUSE){
                    mVideoView.start();
                    mPlayState = PlayState.PLAY;
                    mHander.post(mHideSmallPannelRunable);
                    if(mMediaControl.getPannelVisisable() == View.VISIBLE)
                        mHander.post(mHidePannelRunable);
                }
            }else if(state == MediaControler.PannelControl.DRAGING){
                Log.i(TAG,"Draging"+dist);
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.layout_video_player2, container, true);
        mContext = getActivity();
        mMediaControl = (MediaControler) rootView.findViewById(R.id.CCMediaControler);
        mVideoView = (CCVideoView) rootView.findViewById(R.id.CCVideoView);
        mMediaControl.setFullscreenMode(true);
        mMediaControl.setMediaControl(mMediaControlImpl);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        removeUpdate();
        //mReusmeTime = mVideoView.getCurrentPosition();
        mResumePlayState = mPlayState;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        updateAll();
        mPlayState = PlayState.PLAY;
        mHander.post(mShowSmallPannelRunable);
    }

    public void playUrl(String uri) {
        mVideoView.setVideoPath(uri);
        mVideoView.start();
        mMediaControl.setAlltime(mVideoView.getDuration());
        updateSkbDuration = mVideoView.getDuration() / 2000;
        updateSkbDuration = updateSkbDuration < 40 ? 40 : updateSkbDuration;
        mResumePlayState=PlayState.PLAY;
        mHander.post(mHidePannelRunable);
    }


}
