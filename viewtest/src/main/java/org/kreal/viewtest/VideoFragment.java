package org.kreal.viewtest;


import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.identity.intents.AddressConstants;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class VideoFragment extends Fragment implements Handler.Callback ,View.OnClickListener{

    private static final String TAG = "ViewTest";
    private VideoView mVideoPlay = null;
    private ImageButton full_button;
    private RelativeLayout mControlPannel;
    private SeekBar seekBar;
    private GestureDetector detector;
    private Handler hander;
    private TextView currentTime;
    private TextView allTime;
    private ImageButton btnBack;
    private ImageButton mPause;

    private final int ViedoControlCode=0x0E;
    private final String DoubleTap="DOUBLETAP";
    private final String SingleTap="SINGLETAP";
    private final String Scroll="scroll";
    private final String Event="Event";

    private final int Hander_Update_Seekba=0xef;
    private final int mHideDelay=3000;

    private Uri videosource=null;
    private int mVideoCurrentPlay=0;
    private boolean isFullScreen=false;

    private boolean mResumeIsPlaying=true;
    private int mResumeControlPannlIsVisiable=View.INVISIBLE;

    private final Runnable mHideRunnable=new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private final Runnable mHidePauseRunable=new Runnable() {
        @Override
        public void run() {
            mPause.setVisibility(View.INVISIBLE);
        }
    };

    private final Runnable mShowRunable=new Runnable() {
        @Override
        public void run() {
            show();
        }
    };

    private final Runnable mUpdateSkbRunable=new Runnable() {
        @Override
        public void run() {
            updateSkb();
        }
    };


    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "create");
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "creatview");
        View view=inflater.inflate(R.layout.fragment_video,container,false);
        initViewId(view);
        if(hander==null) {
            hander = new Handler(this);
        }
        ViewGestureListener viewGestureListener=new ViewGestureListener(hander);
        detector= new GestureDetector(getActivity().getApplicationContext(),viewGestureListener);
        detector.setOnDoubleTapListener(viewGestureListener);
        mVideoPlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        Log.i(TAG, "creatviewfinal");
        return view;
    }

    void initViewId(View v){
        mVideoPlay=(VideoView)v.findViewById(R.id.videoView);
        full_button=(ImageButton)v.findViewById(R.id.bottom_fullscreen);
        mControlPannel=(RelativeLayout)v.findViewById(R.id.video_control_pannel);
        seekBar=(SeekBar)v.findViewById(R.id.bottom_seekbar);
        currentTime=(TextView)v.findViewById(R.id.bottom_time_current);
        allTime=(TextView)v.findViewById(R.id.bottom_time);
        btnBack=(ImageButton)v.findViewById(R.id.bottom_back);
        mPause=(ImageButton)v.findViewById(R.id.play_pause);
        mPause.setVisibility(View.INVISIBLE);

        mPause.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        full_button.setOnClickListener(this);
        seekBar.setMax(2000);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hander.removeCallbacks(mUpdateSkbRunable);
        hander.removeCallbacks(mHideRunnable);
        hander.removeCallbacks(mShowRunable);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onpause");
        hander.removeCallbacks(mUpdateSkbRunable);
        hander.removeCallbacks(mHideRunnable);
        hander.removeCallbacks(mShowRunable);
        mVideoCurrentPlay=mVideoPlay.getCurrentPosition();
        mResumeIsPlaying=mVideoPlay.isPlaying();
        mResumeControlPannlIsVisiable=mControlPannel.getVisibility();
        Log.i(TAG, "onpause"+mResumeControlPannlIsVisiable);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResum");
        if(videosource!=null) {
            mVideoPlay.setVideoURI(videosource);
            mVideoPlay.seekTo(mVideoCurrentPlay);
        }
        if(mResumeIsPlaying)
            mVideoPlay.start();
        else mVideoPlay.pause();
        Log.i(TAG, "Resume "+mResumeControlPannlIsVisiable);
        if(mResumeControlPannlIsVisiable==View.VISIBLE){
            Log.i(TAG, "Resumeplay");
            hander.post(mShowRunable);
            //hander.postDelayed(mUpdateSkbRunable, mHideDelay);
        }
        else hander.post(mHideRunnable);
        //hander.postDelayed(mUpdateSkbRunable, 100);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case ViedoControlCode: handerVideo(msg.getData().getString(Event));break;
            case Hander_Update_Seekba:break;
        }
        return false;
    }

    void handerVideo(String data){
        Log.i(TAG, data);
        if(Objects.equals(data, DoubleTap)) {
            if (mVideoPlay.isPlaying()) {
                mVideoPlay.pause();
                mResumeIsPlaying=false;
                hander.post(mShowRunable);
            } else {
                mVideoPlay.start();
                hander.post(mHideRunnable);
            }
        }
        if(Objects.equals(data, SingleTap)){
            if(mControlPannel.getVisibility()==View.VISIBLE)
                hander.post(mHideRunnable);
            else
                hander.post(mShowRunable);
        }
    }

    void hide(){
        if(isFullScreen) {
            getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        mControlPannel.setVisibility(View.INVISIBLE);
        if(mVideoPlay.isPlaying()||mResumeIsPlaying){
            mPause.setVisibility(View.INVISIBLE);
            mPause.setImageDrawable(getResources().getDrawable(R.drawable.mr_ic_pause_dark));
        }
        else {
            mPause.setVisibility(View.VISIBLE);
            mPause.setImageDrawable(getResources().getDrawable(R.drawable.mr_ic_play_dark));
        }
        hander.removeCallbacks(mShowRunable);
    }

    void show(){
        hander.removeCallbacks(mHideRunnable);
        if(isFullScreen) {
            getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            mControlPannel.setPadding(0, getStatusBarHeight(), 0, 0);
        }
        else mControlPannel.setPadding(0,0,0,0);
        mControlPannel.setVisibility(View.VISIBLE);
        mPause.setVisibility(View.VISIBLE);
        if(mVideoPlay.isPlaying()||mResumeIsPlaying)
            mPause.setImageDrawable(getResources().getDrawable(R.drawable.mr_ic_pause_dark));
        else mPause.setImageDrawable(getResources().getDrawable(R.drawable.mr_ic_play_dark));
        hander.post(mUpdateSkbRunable);
    }

    void updateSkb(){
        long lo= (long) seekBar.getMax() * mVideoPlay.getCurrentPosition() /mVideoPlay.getDuration();
        seekBar.setProgress((int) lo);
        currentTime.setText(getTimeIntToString(mVideoPlay.getCurrentPosition()));
        allTime.setText(getTimeIntToString(mVideoPlay.getDuration()));
        if(mControlPannel.getVisibility()==View.VISIBLE){
            hander.postDelayed(mUpdateSkbRunable, 100);
        }
        //else
        //  hander.removeCallbacks(mUpdateSkbRunable);
    }

    private String getTimeIntToString(int time){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("mm:ss");
        String stime=simpleDateFormat.format(new Date((long)time));
        if(time>3600*1000)
            stime=time/3600/1000+":"+stime;
        return stime;
    }

    private int getStatusBarHeight() {
        Resources resources = getActivity().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public void play(Uri uri){
        Log.i(TAG, "playvideo");
        videosource=uri;
        mVideoPlay.setVideoURI(videosource);
        mVideoPlay.start();
    }

    public void setFullScreen(){
        isFullScreen=true;
    }
    @Override
    public void onClick(View v) {
        if(v==mPause) {
            if (mVideoPlay.isPlaying()) {
                mVideoPlay.pause();
                hander.post(mShowRunable);
            } else
            {
                mVideoPlay.start();
                hander.postDelayed(mHideRunnable,100);
            }
            return;
        }
        if(v==btnBack){
            getActivity().onBackPressed();
            return;
        }
        if(v==full_button){
            if(mVideoPlay.getLayoutParams().height==ViewGroup.LayoutParams.MATCH_PARENT) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                mVideoPlay.setLayoutParams(layoutParams);
            }
            else {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.gravity = Gravity.CENTER;
                mVideoPlay.setLayoutParams(layoutParams);
            }
            return;
        }
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        private boolean isDrag=false;
        int progress;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if(isDrag){
                this.progress= progress * mVideoPlay.getDuration() / seekBar.getMax();
                mVideoPlay.seekTo(this.progress);
                //Log.i("dsf",seekBar.getMax()+"+"+seekBar.getProgress());
                currentTime.setText(getTimeIntToString(mVideoPlay.getCurrentPosition()));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isDrag=true;
            mVideoPlay.pause();
            hander.removeCallbacks(mHideRunnable);
            hander.removeCallbacks(mUpdateSkbRunable);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            isDrag=false;
            mVideoPlay.seekTo(progress);
            mVideoPlay.start();
            hander.postDelayed(mHideRunnable,mHideDelay);
            hander.post(mUpdateSkbRunable);
        }
    }

}
