package org.kreal.viewtest;

import android.app.Activity;
import android.content.Intent;
import android.media.TimedText;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class VideoFragment extends Fragment implements Handler.Callback{

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

    private final int ViedoControlCode=0x0E;
    private final String DoubleTap="DOUBLETAP";
    private final String SingleTap="SINGLETAP";
    private final String Scroll="scroll";
    private final String Event="Event";

    private final int Hander_Update_Seekba=0xef;
    private final int mHideDelay=3000;

    private Uri videosource=null;
    private int mVideoCurrentPlay=0;
    private boolean mResumeIsPlaying=true;
    private int mResumeControlPannlIsVisiable=View.VISIBLE;

    private final Runnable mHideRunnable=new Runnable() {
        @Override
        public void run() {
            hide();
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
        // shi ping
        full_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("video/*"), 1);
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
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        seekBar.setMax(1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK) {
            videosource = data.getData();
            mVideoPlay.setVideoURI(data.getData());
            mVideoPlay.start();
        }
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
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResum");
        mVideoPlay.setVideoURI(videosource);
        mVideoPlay.seekTo(mVideoCurrentPlay);
        if(mResumeIsPlaying)
            mVideoPlay.start();
        else mVideoPlay.pause();
        mControlPannel.setVisibility(mResumeControlPannlIsVisiable);
        hander.postDelayed(mUpdateSkbRunable, 100);
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
        Log.i(TAG,data);
        if(Objects.equals(data, DoubleTap)) {
            if (mVideoPlay.isPlaying()) {
                mVideoPlay.pause();
            } else {
                mVideoPlay.start();
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
        mControlPannel.setVisibility(View.GONE);
        hander.removeCallbacks(mShowRunable);
    }
    void show(){
        mControlPannel.setVisibility(View.VISIBLE);
        hander.removeCallbacks(mHideRunnable);
        hander.postDelayed(mHideRunnable, mHideDelay);
        hander.post(mUpdateSkbRunable);
    }
    void updateSkb(){
        if(mControlPannel.getVisibility()==View.VISIBLE){
            long lo= (long) seekBar.getMax() * mVideoPlay.getCurrentPosition() /mVideoPlay.getDuration();
            seekBar.setProgress((int) lo);
            currentTime.setText(getTimeIntToString(mVideoPlay.getCurrentPosition()));
            allTime.setText(getTimeIntToString(mVideoPlay.getDuration()));
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

    public void play(Uri uri){
        videosource=uri;
        mVideoPlay.setVideoURI(videosource);
        mVideoPlay.start();
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
