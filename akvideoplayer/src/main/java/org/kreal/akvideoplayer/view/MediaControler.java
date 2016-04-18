package org.kreal.akvideoplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import org.kreal.akvideoplayer.Gestures.MyGesture;
import org.kreal.akvideoplayer.R;
import org.kreal.akvideoplayer.utli.DensityUtil;
import org.kreal.akvideoplayer.utli.TimeUtil;

import static org.kreal.akvideoplayer.utli.DensityUtil.getStatusBarHeight;

/**
 * Created by lthee on 2016/1/26.
 */
public class MediaControler extends RelativeLayout {

    private static final String TAG = "MediaControl";
    private Context mContext;
    private LinearLayout mTopPannel;
    private LinearLayout mBottonPannel;
    private ImageButton mBack;
    private TextView mTitle;
    private SeekBar mSeekbar;
    private ImageButton mPlayImgBtn;
    private ImageButton mPauseImgBtn;
    private ProgressBar mProgressbar;
    private RelativeLayout mBigPannel;
    private FrameLayout mSmallPannel;
    private TextView mCurrentTimeTv;
    private TextView mAllTimeTv;
    private MediaControlImpl mMediaControl;

    private boolean isFullscreen=true;

    private MyGesture myGesture;
    private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener(){

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i(TAG, "onScroll");
            mMediaControl.onPannelControl(PannelControl.DRAGING,(e2.getX()-e1.getX())/DensityUtil.getWidthInPx(mContext));
            return true;
            //return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "onDoubleTap");
            mMediaControl.onPannelControl(PannelControl.DOUBLETAP, 0);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i(TAG,"onSingleTapConfirmed");
            mMediaControl.onPannelControl(PannelControl.SINGLETAP,0);
            return true;
        }
    };

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListenerse = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && mMediaControl != null) {
                mMediaControl.onProgressTurn(ProgressState.DOING, 1.0f*progress/seekBar.getMax());
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (mMediaControl != null)
                mMediaControl.onProgressTurn(ProgressState.START, 0);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mMediaControl != null)
                mMediaControl.onProgressTurn(ProgressState.STOP, 0);

        }

    };

    private OnClickListener mOnClickListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.imBack) {
                mMediaControl.goBack();
            } else if (i == R.id.play_btn) {
                mMediaControl.onplay();
            } else if (i == R.id.pause_btn) {
                mMediaControl.onPause();
            }
        }
    };

    public enum ProgressState {
        START, DOING, STOP
    }

    public enum PlayState {
        PLAY, PAUSE ,PREPARING
    }

    public enum  PannelControl{
        SINGLETAP , DOUBLETAP ,DRAGING
    }

    public MediaControler(Context context) {
        super(context);
        initView(context);
    }

    public MediaControler(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MediaControler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public MediaControler(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    public void initView(Context context) {
        mContext = context;
        View.inflate(mContext, R.layout.layout_video_mediacontroler, this);
        //控件id初始化
        mBack = (ImageButton) findViewById(R.id.imBack);
        mTitle = (TextView) findViewById(R.id.tvTitle);
        mSeekbar = (SeekBar) findViewById(R.id.skb);
        mPauseImgBtn = (ImageButton)findViewById(R.id.pause_btn);
        mPlayImgBtn = (ImageButton)findViewById(R.id.play_btn);
        mAllTimeTv = (TextView)findViewById(R.id.bottom_all_time);
        mCurrentTimeTv = (TextView)findViewById(R.id.bottom_time_current);
        mTopPannel = (LinearLayout)findViewById(R.id.topPanel);
        mBottonPannel =(LinearLayout)findViewById(R.id.bottomPanel);
        mSmallPannel =(FrameLayout)findViewById(R.id.small_pannel);
        mBigPannel = (RelativeLayout)findViewById(R.id.control_pannel);
        mProgressbar = (ProgressBar)findViewById(R.id.progressBar);
        //动画初始化

        //事件监听初始化
        mBack.setOnClickListener(mOnClickListener);
        mPlayImgBtn.setOnClickListener(mOnClickListener);
        mPauseImgBtn.setOnClickListener(mOnClickListener);
        mSeekbar.setOnSeekBarChangeListener(mOnSeekBarChangeListenerse);
        mSeekbar.setMax(2000);
        myGesture = new MyGesture(mContext,mGestureListener);
        this.setOnTouchListener(myGesture);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setFullscreenMode(boolean is){
        isFullscreen=is;
        if(isFullscreen){
            mTopPannel.setPaddingRelative(0,getStatusBarHeight(mContext),0,0);
        }else mTopPannel.setPaddingRelative(0,0,0,0);
    }

    public void setMediaControl(MediaControlImpl mediaControl) {
        mMediaControl = mediaControl;
    }

    public void setCurrrentTime(String time){
        mCurrentTimeTv.setText(time);
    }

    public void setCurrrentTime(int time){
        mCurrentTimeTv.setText(TimeUtil.getTimeIntToString(time));
    }

    public void setAlltime(String time){
        mAllTimeTv.setText(time);
    }

    public void setAlltime(int time){
        mAllTimeTv.setText(TimeUtil.getTimeIntToString(time));
    }

    public void setPannelVisiable(int V){
        if(V == getPannelVisisable())
            return;
        if(V==VISIBLE){
/*            ScaleAnimation scaleAnimation=new ScaleAnimation(1.0f,1.0f,getScaleRatio(),1.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(200);
            scaleAnimation.setInterpolator(new DecelerateInterpolator());*/
            Animation animation=AnimationUtils.loadAnimation(mContext, R.anim.anim_enter_from_bottom);
            mBigPannel.setVisibility(V);
            mBigPannel.startAnimation(animation);
        }
        else {
/*            ScaleAnimation scaleAnimation=new ScaleAnimation(1.0f,1.0f,1.0f,getScaleRatio(),Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(200);
            scaleAnimation.setInterpolator(new DecelerateInterpolator());*/
            Animation animation=AnimationUtils.loadAnimation(mContext, R.anim.anim_exit_from_bottom);
            mBigPannel.setVisibility(V);
            mBigPannel.startAnimation(animation);
        }

    }


    public int getPannelVisisable(){
        return mBigPannel.getVisibility();
    }

    public void setSmallPannelVisiable(PlayState state ,int V){

        if (state == PlayState.PAUSE) {
            mPauseImgBtn.setVisibility(INVISIBLE);
            mPlayImgBtn.setVisibility(VISIBLE);
            mProgressbar.setVisibility(INVISIBLE);
        } else if (state == PlayState.PLAY) {
            mPauseImgBtn.setVisibility(VISIBLE);
            mPlayImgBtn.setVisibility(INVISIBLE);
            mProgressbar.setVisibility(INVISIBLE);
        } else if (state == PlayState.PREPARING) {
            mPauseImgBtn.setVisibility(INVISIBLE);
            mPlayImgBtn.setVisibility(INVISIBLE);
            mProgressbar.setVisibility(VISIBLE);
        }
        if(V==getSmallPannelVisiable())
            return;
        if(V==VISIBLE) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f,1.0f);
            alphaAnimation.setDuration(200);
            mSmallPannel.setAnimation(alphaAnimation);;
        }else {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.0f);
            alphaAnimation.setDuration(100);
            mSmallPannel.setAnimation(alphaAnimation);;
        }
        mSmallPannel.setVisibility(V);
    }

    public int getSmallPannelVisiable(){return mSmallPannel.getVisibility();}

    public void updateSeekbar(float lo) {
        mSeekbar.setProgress((int) (lo * mSeekbar.getMax()));
    }

    public void updateCurrentTime(int CurrentTime){
        setCurrrentTime(CurrentTime);
    }

    private float getScaleRatio(){
        float halfhight = DensityUtil.getHeightInPx(mContext)/2;
        float maxbar = mBottonPannel.getHeight()>(isFullscreen?(mTopPannel.getHeight()+DensityUtil.getStatusBarHeight(mContext)):mTopPannel.getHeight())?mBottonPannel.getHeight():mTopPannel.getHeight();
        return (halfhight+maxbar)/halfhight;
    }

    public interface MediaControlImpl {
        void goBack();
        void onStart();
        void onplay();
        void onPause();
        void onProgressTurn(ProgressState state, float progress);
        void onPannelControl(PannelControl state ,float dist);
    }
}
