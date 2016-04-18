package org.kreal.akvideoplayer.Gestures;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by lthee on 2016/2/3.
 */
public class MyGesture implements View.OnTouchListener {
    private boolean isMultTouch = false;
    private Context mContext;
    private GestureDetector mDetector;

    public MyGesture(Context mContext,GestureDetector.SimpleOnGestureListener listener) {
        this.mContext = mContext;
        mDetector = new GestureDetector(this.mContext,listener);
        mDetector.setOnDoubleTapListener(listener);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMultTouch = false;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                isMultTouch = false;
                break;
            default:
                isMultTouch = true;
        }
        if (!isMultTouch)
            mDetector.onTouchEvent(event);
        return true;
    }
}
