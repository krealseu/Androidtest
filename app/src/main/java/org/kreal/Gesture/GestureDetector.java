package org.kreal.Gesture;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;


/**
 * Created by lthee on 2016/9/4.
 */
public class GestureDetector {
    private static final long DOUBLE_TAP_TIMEOUT = 300;
    private static final long DOUBLE_TAP_MIN_TIME = 100;
    final private String TAG = GestureDetector.class.getSimpleName();
    final private int TAP = 1;
    final private int DOUBLETAP = 2;
    private final Handler mHandler = new PHHandler();

    private long lastEventTime = 0;
    public boolean onTouchEvent(MotionEvent ev){
//        if((ev.getAction()&MotionEvent.ACTION_MASK)==2)
//            return true;
//        Log.i(TAG,""+(ev.getAction()&MotionEvent.ACTION_MASK)+"  id:");
        switch (ev.getAction()&MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                boolean hadTapMessage = mHandler.hasMessages(TAP);
                if (hadTapMessage) mHandler.removeMessages(TAP);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
    private boolean isConsideredDoubleTap(MotionEvent firstDown, MotionEvent firstUp,
                                          MotionEvent secondDown) {

        final long deltaTime = secondDown.getEventTime() - firstUp.getEventTime();
        if (deltaTime > DOUBLE_TAP_TIMEOUT || deltaTime < DOUBLE_TAP_MIN_TIME) {
            return false;
        }

        int deltaX = (int) firstDown.getX() - (int) secondDown.getX();
        int deltaY = (int) firstDown.getY() - (int) secondDown.getY();
        return (deltaX * deltaX + deltaY * deltaY < 222222);
    }

    private class PHHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {

        }
    }
}
